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
import kotlinx.android.synthetic.main.activity_sell_navi4.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.util.ArrayList
import kotlin.collections.HashMap

class SellNavi4Activity : AppCompatActivity() {

    var mSoldOutPost = Post()

    lateinit var currentUid: String

    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null

    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_navi4)

        val bundle = intent.extras
        val post = bundle.getSerializable(IntentKey.POST.name) as Post

        currentUid = getCurrentUser()!!.uid

        backArrow.setOnClickListener {
            finish()
        }

        Api.User.observeUser(currentUid!!, {

            it?.let {
                mCurrentUserModel = it
            }
        })


        Api.SoldOut.observeSoldOutPost(post.id!!, { soldOutPost ->


            soldOutPost?.let {

                Api.User.observeUser(it.purchaserUid!!, {

                    it?.let {
                        mUsermodel = it
                        setOppositeUserInfo(it)
                    }
                })
                mSoldOutPost = it
                setPost()
                observeChats()
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

    }

    fun goToUserProfile() {
        mUsermodel?.let {
            val intent = Intent(this@SellNavi4Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
    }

    fun setPost() {
        titleTv.text = mSoldOutPost.title
        Picasso.get()
            .load(Uri.parse(mSoldOutPost.thumbnailUrl))
            .into(postIv)

        priceTv1.text = getFormatPrice(mSoldOutPost.price!!)+ "円"
        shipPayerTv.text = mSoldOutPost.shipPayer
        priceTv2.text = getFormatPrice(mSoldOutPost.price!!)+ "円"

        //
        val timestamp = mSoldOutPost.timestamp
        val date = getDateFromTimestamp(timestamp!!)
        transactionCompleDateTv.text = "取引完了日: " + date

        priceTv2.text = getFormatPrice(mSoldOutPost.price!!)+ "円"
        val commision = mSoldOutPost.price!!.toFloat() * mSoldOutPost.commisionRate!!
        commisionTv.text = "-" + getFormatPrice(commision.toInt())
        profitTv.text = getFormatPrice(mSoldOutPost.price!! - commision.toInt()) +"円"

        Api.Value.fetchMyValues(currentUid, { valuemodel, valueCount ->

            valuemodel?.let {

                if (it.postId == mSoldOutPost.id) {

                    if(it.valueStatus == Value.sun.toString()) {
                        valueIv.setImageResource(R.drawable.sun)
                        valueTv.text = "よい評価"
                    } else if (it.valueStatus == Value.cloud.toString()) {
                        valueIv.setImageResource(R.drawable.cloud)
                        valueTv.text = "ふつうの評価"
                    } else if (it.valueStatus == Value.rain.toString()) {
                        valueIv.setImageResource(R.drawable.rain)
                        valueTv.text = "わるい評価"
                    }

                    val charReplace = "\n"
                    val comment = it.valueComment?.replace(charReplace, "")
                    valueCommentTv.text = comment
                }
            }
        })
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

    fun observeChats() {

        var count = 0
        Api.Chat.observeChats(mSoldOutPost.id!!, {chat, chatCount ->

            count++
            chat?.let {

                chats.add(0, it)
            }

            if (count == chatCount) {

                setRecyclerView()
            }
        })
    }

    fun chatTouchUpInside(v: View?) {

        val message = chatEdit.text.toString()

        if (message.equals("")) {
            IOSDialog.Builder(this)
                .setPositiveButton("OK",null)
                .setMessage("メッセージを空白にはできません。").show()
            return
        }

        val autoKey = Api.Chat.REF_CHATS.child(mSoldOutPost.id).push().key
        val timestamp = getTimestamp()
        val date = getDateFromTimestamp(timestamp)

        var result = HashMap<String, Any>()
        result.put("date", date)
        result.put("timestamp", timestamp)
        result.put("messageText", message)
        result.put("uid", currentUid!!)

        Api.Chat.REF_CHATS.child(mSoldOutPost.id).child(autoKey).setValue(result)

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
