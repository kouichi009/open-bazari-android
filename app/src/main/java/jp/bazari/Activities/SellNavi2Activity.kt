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
import kotlinx.android.synthetic.main.activity_sell_navi2.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.util.*

class SellNavi2Activity : AppCompatActivity() {

    var mPost = Post()
    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null
    var currentUid: String? = null
    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_navi2)

        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post

        currentUid = getCurrentUser()!!.uid

        backArrow.setOnClickListener {
            finish()
        }

        Api.User.observeUser(mPost.purchaserUid!!, {

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

    fun setOppositeUserInfo(usermodel: UserModel) {

        Picasso.get()
            .load(Uri.parse(usermodel.profileImageUrl))
            .into(profileIv)
        usernameTv.text = usermodel.username

        sunTv.text = usermodel.sun.toString()
        cloudTv.text = usermodel.cloud.toString()
        rainTv.text = usermodel.rain.toString()
    }

    fun goToUserProfile() {
        mUsermodel?.let {
            val intent = Intent(this@SellNavi2Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
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


    fun load() {

        val timestamp = mPost.shippedDateTimestamp

        shippedDateTv.text = "発送通知日: " + getDateFromTimestamp(timestamp!!)

        Api.Address.observeAddress(mPost.purchaserUid!!, { address ->

            address?.let {
                postalCodeTv.text = "〒"+it.postalCode

                var addressStr = it.prefecure + it.city + it.tyou
                it.building?.let { building ->
                    addressStr = addressStr + building
                }
                addressTv.text = addressStr

                nameTv.text = it.seiKanji + it.meiKanji
            }
        })

        shipWayTv.text = "配送方法: "+mPost.shipWay
        shipDeadLineTv.text = "発送期限: "+mPost.shipDeadLine

        priceTv1.text = getFormatPrice(mPost.price!!)+"円"
        priceTv2.text = getFormatPrice(mPost.price!!)+ "円"

        val commision = mPost.price!!.toFloat() * mPost.commisionRate!!
        commisionTv.text = "-" + getFormatPrice(commision.toInt())
        profitTv.text = getFormatPrice(mPost.price!! - commision.toInt()) + "円"

        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        shipPayerTv.text = mPost.shipPayer
        titleTv.text = mPost.title

    }

    fun chatTouchUpInside(v: View?) {

        val message = chatEdit.text.toString()

        if (message.equals("")) {
            IOSDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setMessage("メッセージを空白にはできません。").show()
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


        //メッセージが一個もなかった場合は、adapterをセットする必要がある。
        //はじめてメッセージしたら、chatsのsizeが１になるので、1であれば、アダプターをセットする。
        if (chats.size <= 1) {

            setRecyclerView()
            return
        }

        mAdapter?.notifyDataSetChanged()
    }

    fun setRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val mLayouManager = LinearLayoutManager(this)
        mAdapter = ChatItemAdapter(this, chats, mUsermodel, mCurrentUserModel)
        recyclerView.setLayoutManager(mLayouManager)
        recyclerView.setAdapter(mAdapter)
    }
}
