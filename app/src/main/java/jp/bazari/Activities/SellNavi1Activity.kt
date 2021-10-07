package jp.bazari.Activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import com.squareup.picasso.Picasso
import jp.bazari.Adapters.ChatItemAdapter
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Chat
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_sell_navi1.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SellNavi1Activity : AppCompatActivity() {

    var mPost = Post()
    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null
    var currentUid: String? = null
    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null

    var fromActivity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_navi1)

        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post
        fromActivity = bundle.getString(IntentKey.FROMACTIVITY.name)

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

        sendNoti.setOnClickListener {

            if(mUsermodel == null || mCurrentUserModel == null) {
                return@setOnClickListener
            }

            sendNoti.isEnabled = false

            showAlertDialog("商品の発送は終わっていますか？", "必ず商品の発送が終わってから発送通知を行ってください。","発送した", "キャンセル")
        }

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
            val intent = Intent(this@SellNavi1Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
    }


    fun load() {

        //支払完了日
        val timestamp = mPost.purchaseDateTimestamp
        val date = getDateFromTimestamp(timestamp!!)
        payCompleDateTv.text = "支払完了日: " + date

        //発送期限
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

        shipDeadLineDateTv.text = "発送期限: " + result + "までに発送"

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

        shipWayTv1.text = mPost.shipWay
        shipWayTv2.text = mPost.shipWay

        shipDeadLineTv.text = mPost.shipDeadLine

        priceTv1.text = getFormatPrice(mPost.price!!)+"円"
        priceTv2.text = getFormatPrice(mPost.price!!)+"円"

        val commision = mPost.price!!.toFloat() * mPost.commisionRate!!
        commisionTv.text = "-" + getFormatPrice(commision.toInt())
        profitTv.text = getFormatPrice(mPost.price!! - commision.toInt())+"円"

        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        shipPayerTv.text = mPost.shipPayer
        titleTv.text = mPost.title

    }

    fun showAlertDialog(title: String, message: String, positiveStr: String, negativeStr: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(positiveStr) {dialog, which ->
            dialog.dismiss()
            update()
        }

        builder.setNegativeButton(negativeStr) { dialog, which ->
            dialog.dismiss()
            sendNoti.isEnabled = true
        }

        builder.show().apply {

            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.link_blue))
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

        }
    }

    fun update() {

        var result = HashMap<String, Any>()
        result.put("shippedDateTimestamp", getTimestamp())
        result.put(getString(R.string.sellerShouldDo), waitCatch)
        result.put(getString(R.string.purchaserShouldDo), catchProduct)

        Api.Post.REF_POSTS.child(mPost.id!!)
            .updateChildren(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                sendNoti.isEnabled = true
                if (databaseError != null) {
                    makeToast(this@SellNavi1Activity, databaseError.toString())
                    return@CompletionListener
                }


                Api.MySellPosts.REF_MYSELL.child(getCurrentUser()!!.uid).child(mPost.id!!)
                    .child("timestamp").setValue(getTimestamp())
                Api.MyPurchasePosts.REF_MYPURCHASE.child(mPost.purchaserUid!!).child(mPost.id!!)
                    .child(getString(R.string.timestamp)).setValue(getTimestamp())


                setNotification()

                val fcmToken = mUsermodel?.fcmToken
                fcmToken?.let {

                    AsyncTask.execute {  sendPushNotification(it) }

                }

                if (fromActivity.equals(getString(R.string.NotificationActivity)))
                    notificationActivity?.updateNotificationAC()

                else if (fromActivity.equals(getString(R.string.SellListActivity)))
                    sellListActivity?.updateSellListAC()


                finish()
            })
    }

    fun setNotification() {

        val timestamp = getTimestamp()
        val newNotificationReference = Api.Notification.REF_NOTIFICATION
        val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost.purchaserUid)
        val newNotificationId = newNotificationReference.push().key

        var result = HashMap<String, Any>()
        result.put("checked", false)
        result.put("from", currentUid!!)
        result.put("objectId", mPost.id!!)
        result.put("type", Type.naviShip)
        result.put("timestamp", timestamp)
        result.put("to", mPost.purchaserUid!!)
        result.put("segmentType", TransactionStatus.transaction)

        newNotificationReference.child(newNotificationId).setValue(result)
        newMyNotificationReference.child(newNotificationId).child("timestamp").setValue(timestamp)

    }

    fun sendPushNotification(token: String) {

        //表示タイトルの文字数の限界を設定
        val indexNum = pushNotiTitleCount
        var titleStr: String = mPost.title!!

        if (titleStr.count() > indexNum) {
            titleStr = titleStr.substring(0, indexNum)
            titleStr = titleStr+"..."
        }
        val message = "「" + titleStr + "」が発送されました。商品が到着したら受取通知を行ってください。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        },{

            //onError


        })

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


        //コメントが一個もなかった場合は、adapterをセットする必要がある。
        //はじめてコメントしたら、commentsのsizeが１になるので、1であれば、アダプターをセットする。
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
