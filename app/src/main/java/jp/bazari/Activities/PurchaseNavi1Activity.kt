package jp.bazari.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ligl.android.widget.iosdialog.IOSDialog
import com.squareup.picasso.Picasso
import jp.bazari.Adapters.ChatItemAdapter
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Chat
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_purchase_navi1.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.text.SimpleDateFormat
import java.util.*


class PurchaseNavi1Activity : AppCompatActivity() {

    private val TAG = "PurchaseNavi1Activity"
    var mPost = Post()
    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null
    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null

    lateinit var currentUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_navi1)

        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post

        backArrow.setOnClickListener {
            finish()
        }

        var chatList: ArrayList<Chat> = ArrayList()
        //   setFirebase(postId)
        //    return;

        currentUid = getCurrentUser()!!.uid

        Api.User.observeUser(mPost.uid!!, {

            it?.let {
                mUsermodel = it
                setOppositeUserInfo(it)
            }
        })

        Api.User.observeUser(currentUid!!, {

            it?.let {
                mCurrentUserModel = it
            }
        })

        profileIv.setOnClickListener {
            goToUserProfile()
        }

        usernameTv.setOnClickListener {
            goToUserProfile()
        }

        valueLayout.setOnClickListener {
            goToUserProfile()
        }

        observeChats()
        load()
    }

    fun goToUserProfile() {
        mUsermodel?.let {
            val intent = Intent(this@PurchaseNavi1Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
    }

    fun setOppositeUserInfo(usermodel: UserModel) {

        Picasso.get()
            .load(Uri.parse(usermodel.profileImageUrl))
            .into(profileIv)
        usernameTv.text = usermodel.username

        sunTv.text = usermodel.sun.toString()
        cloudTv.text = usermodel.cloud.toString()
        rainTv.text = usermodel.rain.toString()
    }

    fun load() {
        //???????????????
        val timestamp = mPost.purchaseDateTimestamp
        val date0 = getDateFromTimestamp(timestamp!!)
        payCompleDateTv.text = "???????????????: " + date0

        //????????????
        var dateStr = getHyphenDateFromTimestamp(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        c.time = sdf.parse(dateStr)
        var laterDays = 0
        if (mPost.shipDeadLine == shipDatesList[0]) {
            laterDays = 2
        } else if (mPost.shipDeadLine == shipDatesList[1]) {
            laterDays = 3
        } else if (mPost.shipDeadLine == shipDatesList[2]) {
            laterDays = 7
        }

        c.add(Calendar.DATE, laterDays)  // number of days to add
        val hyphenDate = sdf.format(c.time)
        val result = getDateFromHyphen(hyphenDate)

        shipDeadLineDateTv.text = "????????????: " + result + "?????????????????????"

        Api.Address.observeAddress(currentUid, { address ->

            address?.let {
                postalCodeTv.text = "???"+it.postalCode

                var addressStr = it.prefecure + it.city + it.tyou
                it.building?.let { building ->
                    addressStr = addressStr + building
                }
                addressTv.text = addressStr

                nameTv.text = it.seiKanji + it.meiKanji
            }
        })

        shipWayTv.text = mPost.shipWay

        shipDeadLineTv.text = mPost.shipDeadLine

        priceTv1.text = getFormatPrice(mPost.price!!)+"???"
        priceTv2.text = getFormatPrice(mPost.price!!)+"???"

        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        shipPayerTv.text = mPost.shipPayer
        titleTv.text = mPost.title

    }

    fun observeChats() {

        var count = 0
        Api.Chat.observeChats(mPost.id!!, {chat, chatCount ->

            count++
            chat?.let {

                chats.add(0, it)
            }

            if (count == chatCount) {

                setRecyclerView()
            }
        })
    }

    fun setRecyclerView() {
     //   recyclerView.setHasFixedSize(true)
        val mLayouManager = LinearLayoutManager(this)
        mAdapter = ChatItemAdapter(this, chats, mUsermodel, mCurrentUserModel)
        recyclerView.setLayoutManager(mLayouManager)
        recyclerView.setAdapter(mAdapter)
    }

    fun chatTouchUpInside(v: View?) {

        val message = chatEdit.text.toString()

        if (message.equals("")) {
            IOSDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setMessage("????????????????????????????????????????????????").show()
            return
        }

        val autoKey = Api.Chat.REF_CHATS.child(mPost.id).push().key
        val timestamp = getTimestamp()
        val date = getDateFromTimestamp(timestamp)

        var result = HashMap<String, Any>()
        result.put("date", date)
        result.put("timestamp", timestamp)
        result.put("messageText", message)
        result.put("uid", currentUid!!)

        Api.Chat.REF_CHATS.child(mPost.id).child(autoKey).setValue(result)

        val chat = Chat.transformChat(result, autoKey)

        chats.add(0, chat)

        chatEdit.setText("")

        //???????????????????????????????????????????????????adapter????????????????????????????????????
        //???????????????????????????????????????chats???size????????????????????????1???????????????????????????????????????????????????
        if (chats.size <= 1) {

            setRecyclerView()
            return
        }

        mAdapter?.notifyDataSetChanged()
    }
}
