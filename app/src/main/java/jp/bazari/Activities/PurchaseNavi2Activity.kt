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
import android.widget.RadioButton
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import com.squareup.picasso.Picasso
import jp.bazari.Adapters.ChatItemAdapter
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Chat
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_purchase_navi2.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.text.SimpleDateFormat
import java.util.*

class PurchaseNavi2Activity : AppCompatActivity() {

    var currentRadioButton: RadioButton? = null

    var valueStatus = Value.sun.toString()

    var mPost = Post()
    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null
    var currentUid: String? = null
    var isChecked: Boolean = false

    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null

    var fromActivity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_navi2)

        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post
        fromActivity = bundle.getString(IntentKey.FROMACTIVITY.name)

        backArrow.setOnClickListener {
            finish()
        }

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

        goodTextColor()
        valueBtnStatus(false)

        checkBox.setOnCheckedChangeListener { view, isChecked ->
            this.isChecked = isChecked
            valueBtnStatus(isChecked)
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
            val intent = Intent(this@PurchaseNavi2Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
    }

    fun load() {

        //支払完了日
        val timestamp = mPost.purchaseDateTimestamp
        val date0 = getDateFromTimestamp(timestamp!!)
        payCompleDateTv.text = "支払完了日: " + date0

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

        shipDeadLineDateTv.text = "発送期限: " + result + "までに発送予定"

        Api.Address.observeAddress(currentUid!!, { address ->

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
        priceTv2.text = getFormatPrice(mPost.price!!)+"円"

        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        shipPayerTv.text = mPost.shipPayer
        titleTv.text = mPost.title

    }



    fun radioButton(v: View) {

        val radioId = radioGroup.checkedRadioButtonId

        currentRadioButton = findViewById<RadioButton>(radioId)

        if (currentRadioButton!!.getText() == getString(R.string.good_seller)) {
            goodTextColor()
        } else if (currentRadioButton!!.getText() == getString(R.string.normal_seller)) {
            normalTextColor()
        } else if (currentRadioButton!!.getText() == getString(R.string.bad_seller)) {
            badTextColor()
        }

    }

    fun goodTextColor() {

        tv1.setTextColor(Color.parseColor("#FF3D3A"))
        tv2.setTextColor(Color.parseColor("#FF3D3A"))
        tv3.setTextColor(Color.parseColor("#555555"))
        tv4.setTextColor(Color.parseColor("#555555"))
        tv5.setTextColor(Color.parseColor("#555555"))

        valueStatus = Value.sun.toString()
    }

    fun normalTextColor() {
        tv1.setTextColor(Color.parseColor("#555555"))
        tv2.setTextColor(Color.parseColor("#555555"))
        tv3.setTextColor(Color.parseColor("#FF3D3A"))
        tv4.setTextColor(Color.parseColor("#FF3D3A"))
        tv5.setTextColor(Color.parseColor("#555555"))

        valueStatus = Value.cloud.toString()
    }

    fun badTextColor() {
        tv1.setTextColor(Color.parseColor("#555555"))
        tv2.setTextColor(Color.parseColor("#555555"))
        tv3.setTextColor(Color.parseColor("#555555"))
        tv4.setTextColor(Color.parseColor("#555555"))
        tv5.setTextColor(Color.parseColor("#FF3D3A"))

        valueStatus = Value.rain.toString()
    }

    fun valueBtnStatus(status: Boolean) {

        if (status) {
            valueBtn.isEnabled = true
            valueBtn.setBackgroundColor(Color.parseColor("#FF3D3A"))
            valueBtn.setTextColor(Color.parseColor("#ffffff"))
        } else {
            valueBtn.isEnabled = false
            valueBtn.setBackgroundColor(Color.parseColor("#bfbfbf"))
            valueBtn.setTextColor(Color.parseColor("#555555"))
        }
    }

    fun valueTouchUpInside(v: View) {

        if (mUsermodel == null || mCurrentUserModel == null) {
            return
        }

        valueBtn.isEnabled = false

        showAlertDialog("評価を投稿しますか？", "一度投稿した評価は変更できませんのでご注意ください。", "評価を投稿する", "キャンセル")

    }


    fun showAlertDialog(title: String, message: String, yesStr: String, noStr: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            builder.setCancelable(false)
            builder.setMessage(title)
            builder.setMessage(message)

        builder.setPositiveButton(yesStr) { dialog, which ->
            dialog.dismiss()

            positive()
        }


        builder.setNegativeButton(noStr) { dialog, which ->
            dialog.dismiss()
            valueBtn.isEnabled = true
        }

        builder.show()
    }

    fun positive() {


        val timestamp = getTimestamp()

        val autoId = Api.Value.REF_MYVALUE.child(mUsermodel!!.id!!).push().key
        Api.Value.REF_MYVALUE.child(mUsermodel!!.id!!).child(autoId).child("timestamp").setValue(timestamp)

        val valueComment = valueCommentEdit.text.toString()

        var result = HashMap<String, Any>()
        result.put("valueStatus", valueStatus)
        result.put("valueComment", valueComment)
        result.put("from", currentUid!!)
        result.put("type", "sell")
        result.put("postId", mPost.id!!)
        result.put("timestamp", timestamp)

        Api.Value.REF_VALUE.child(autoId).setValue(result)

        if (valueStatus == Value.sun.toString()) {
            var sunCount = (mUsermodel!!.sun!!)
            sunCount += 1
            var result = HashMap<String, Any>()
            result.put(valueStatus, sunCount)
            Api.User.REF_USERS.child(mUsermodel!!.id!!).child("value").updateChildren(result)

        } else if (valueStatus == Value.cloud.toString()) {
            var cloudCount = (mUsermodel!!.cloud!!)
            cloudCount += 1
            var result = HashMap<String, Any>()
            result.put(valueStatus, cloudCount)
            Api.User.REF_USERS.child(mUsermodel!!.id!!).child("value").updateChildren(result)
        } else {
            var rainCount = (mUsermodel!!.rain!!)
            rainCount += 1
            var result = HashMap<String, Any>()
            result.put(valueStatus, rainCount)
            Api.User.REF_USERS.child(mUsermodel!!.id!!).child("value").updateChildren(result)
        }


        var result2 = HashMap<String, Any>()
        result2.put(getString(R.string.sellerShouldDo), valueBuyer)
        result2.put(getString(R.string.purchaserShouldDo), waitForValue)
        result2.put(getString(R.string.transactionStatus), getString(R.string.transaction))


        Api.Post.REF_POSTS.child(mPost!!.id!!)
            .updateChildren(result2, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                valueBtn.isEnabled = true

                if (databaseError != null) {

                    makeToast(this@PurchaseNavi2Activity, databaseError.toString())
                    return@CompletionListener
                }


                Api.MySellPosts.REF_MYSELL.child(mPost!!.uid!!).child(mPost!!.id!!)
                    .child(getString(R.string.timestamp)).setValue(getTimestamp())

                Api.MyPurchasePosts.REF_MYPURCHASE.child(getCurrentUser()!!.uid).child(mPost!!.id!!)
                    .child(getString(R.string.timestamp)).setValue(
                        getTimestamp()
                    )


                setNotification()

                val fcmToken = mUsermodel?.fcmToken
                fcmToken?.let {

                    AsyncTask.execute { sendPushNotification(it) }

                }

                if (fromActivity.equals(getString(R.string.NotificationActivity)))
                    notificationActivity?.updateNotificationAC()

                else if (fromActivity.equals(getString(R.string.PurchaseListActivity)))
                    purchaseListActivity?.updatePurchaseListAC()

                finish()

            })
    }

    fun setNotification() {

        val timestamp = getTimestamp()
        val newNotificationReference = Api.Notification.REF_NOTIFICATION
        val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost.uid!!)
        val newNotificationId = newNotificationReference.push().key

        var result = HashMap<String, Any>()
        result.put("checked", false)
        result.put("from", currentUid!!)
        result.put("objectId", mPost.id!!)
        result.put("type", Type.naviEvaluatePurchaser)
        result.put("timestamp", timestamp)
        result.put("to", mPost.uid!!)
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
            titleStr = titleStr + "..."
        }
        val message = "「" + titleStr + "」の受取通知がありました。購入者の評価を行い、取引を完了させてください。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        }, {

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

    fun setRecyclerView() {
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
}
