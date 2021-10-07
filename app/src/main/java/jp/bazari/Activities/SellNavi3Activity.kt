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
import kotlinx.android.synthetic.main.activity_sell_navi3.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.util.ArrayList
import kotlin.collections.HashMap


class SellNavi3Activity : AppCompatActivity() {

    var currentRadioButton: RadioButton? = null

    var valueStatus = Value.sun.toString()

    var mPost = Post()
    var mUsermodel: UserModel? = null
    var mCurrentUserModel: UserModel? = null
    var currentUid: String? = null
    var chats: ArrayList<Chat> = ArrayList()

    var mAdapter: ChatItemAdapter? = null

    var fromActivity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_navi3)

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

        goodTextColor()

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

    fun load() {
        titleTv.text = mPost.title
        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        priceTv1.text = getFormatPrice(mPost.price!!)+"円"
        shipPayerTv.text = mPost.shipPayer
        priceTv2.text = getFormatPrice(mPost.price!!)+"円"
        shipWayTv.text = "配送方法: "+mPost.shipWay
        shipDeadLineTv.text = "発送期限: "+mPost.shipDeadLine

        val commision = mPost.price!!.toFloat() * mPost.commisionRate!!
        commisionTv.text = "-" + getFormatPrice(commision.toInt())
        profitTv.text = getFormatPrice(mPost.price!! - commision.toInt())+"円"
    }

    fun goToUserProfile() {
        mUsermodel?.let {
            val intent = Intent(this@SellNavi3Activity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, it.id)
            }
            startActivity(intent)
        }
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


    fun valueTouchUpInside(v: View) {

        if(mUsermodel == null || mCurrentUserModel == null) {
            return
        }

        valueBtn.isEnabled = false

        showAlertDialog("評価を投稿しますか？", "一度投稿した評価は変更できませんのでご注意ください。", "評価を投稿する", "キャンセル")

    }

    fun showAlertDialog(title: String, message: String, yesStr: String, noStr: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
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

    fun setNotification() {

        val timestamp = getTimestamp()
        val newNotificationReference = Api.Notification.REF_NOTIFICATION
        val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost.purchaserUid)
        val newNotificationId = newNotificationReference.push().key

        var result = HashMap<String, Any>()
        result.put("checked", false)
        result.put("from", currentUid!!)
        result.put("objectId", mPost.id!!)
        result.put("type", Type.naviEvaluateSeller)
        result.put("timestamp", timestamp)
        result.put("to", mPost.purchaserUid!!)
        result.put("segmentType", TransactionStatus.transaction)

        newNotificationReference.child(newNotificationId).setValue(result)
        newMyNotificationReference.child(newNotificationId).child("timestamp").setValue(timestamp)

    }

    fun positive() {

        val valueComment = valueCommentEdit.text.toString()

        val timestamp = getTimestamp()

        val autoId = Api.Value.REF_MYVALUE.child(mPost.purchaserUid!!).push().key
        Api.Value.REF_MYVALUE.child(mPost.purchaserUid!!).child(autoId).child("timestamp").setValue(timestamp)

        var result = HashMap<String, Any>()
        result.put("valueStatus", valueStatus)
        result.put("valueComment", valueComment)
        result.put("from", currentUid!!)
        result.put("type", "purchase")
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

        var soldHashMap = HashMap<String, Any>()
        when (mPost.imageCount!! - 1) {
            0 -> {
                soldHashMap.put("thumbnail", mPost.thumbnailUrl!!)
                soldHashMap.put("thumbnailRatio", mPost.thumbnailRatio!!)
                soldHashMap.put("commisionRate", mPost.commisionRate!!)
                soldHashMap.put("uid", currentUid!!)
                soldHashMap.put("title", mPost.title!!)
                soldHashMap.put("caption", mPost.caption!!)
                soldHashMap.put("timestamp", timestamp)
                soldHashMap.put("imageCount", mPost.imageCount!!)
                soldHashMap.put("category1", mPost.category1!!)
                soldHashMap.put("category2", mPost.category2!!)
                soldHashMap.put("status", mPost.status!!)
                soldHashMap.put("shipPayer", mPost.shipPayer!!)
                soldHashMap.put("shipWay", mPost.shipWay!!)
                soldHashMap.put("shipDeadLine", mPost.shipDeadLine!!)
                soldHashMap.put("shippedDateTimestamp", mPost.shippedDateTimestamp!!)
                soldHashMap.put("shipFrom", mPost.shipFrom!!)
                soldHashMap.put("price", mPost.price!!)
                soldHashMap.put("transactionStatus", "sold")
                soldHashMap.put("sellerShouldDo", soldFinish)
                soldHashMap.put("purchaserUid", mPost.purchaserUid!!)

                soldHashMap.put("image0", mPost.imageUrls[0])
                soldHashMap.put("ratio0", mPost.ratios[0])

                mPost.likes?.let {
                    soldHashMap.put("likes", it)
                }
                mPost.comments?.let {
                    soldHashMap.put("comments", it)
                }
                mPost.likeCount?.let {
                    soldHashMap.put("likeCount", it)
                }
                mPost.commentCount?.let {
                    soldHashMap.put("commentCount", it)
                }
                mPost.brand?.let {
                    soldHashMap.put("brand", mPost.brand!!)

                }
            }

            1 -> {
                soldHashMap.put("thumbnail", mPost.thumbnailUrl!!)
                soldHashMap.put("thumbnailRatio", mPost.thumbnailRatio!!)
                soldHashMap.put("commisionRate", mPost.commisionRate!!)
                soldHashMap.put("uid", currentUid!!)
                soldHashMap.put("title", mPost.title!!)
                soldHashMap.put("caption", mPost.caption!!)
                soldHashMap.put("timestamp", timestamp)
                soldHashMap.put("imageCount", mPost.imageCount!!)
                soldHashMap.put("category1", mPost.category1!!)
                soldHashMap.put("category2", mPost.category2!!)
                soldHashMap.put("status", mPost.status!!)
                soldHashMap.put("shipPayer", mPost.shipPayer!!)
                soldHashMap.put("shipWay", mPost.shipWay!!)
                soldHashMap.put("shipDeadLine", mPost.shipDeadLine!!)
                soldHashMap.put("shippedDateTimestamp", mPost.shippedDateTimestamp!!)
                soldHashMap.put("shipFrom", mPost.shipFrom!!)
                soldHashMap.put("price", mPost.price!!)
                soldHashMap.put("transactionStatus", "sold")
                soldHashMap.put("sellerShouldDo", soldFinish)
                soldHashMap.put("purchaserUid", mPost.purchaserUid!!)

                soldHashMap.put("image0", mPost.imageUrls[0])
                soldHashMap.put("ratio0", mPost.ratios[0])
                soldHashMap.put("image1", mPost.imageUrls[1])
                soldHashMap.put("ratio1", mPost.ratios[1])

                mPost.likes?.let {
                    soldHashMap.put("likes", it)
                }
                mPost.comments?.let {
                    soldHashMap.put("comments", it)
                }
                mPost.likeCount?.let {
                    soldHashMap.put("likeCount", it)
                }
                mPost.commentCount?.let {
                    soldHashMap.put("commentCount", it)
                }
                mPost.brand?.let {
                    soldHashMap.put("brand", mPost.brand!!)

                }
            }

            2 -> {
                soldHashMap.put("thumbnail", mPost.thumbnailUrl!!)
                soldHashMap.put("thumbnailRatio", mPost.thumbnailRatio!!)
                soldHashMap.put("commisionRate", mPost.commisionRate!!)
                soldHashMap.put("uid", currentUid!!)
                soldHashMap.put("title", mPost.title!!)
                soldHashMap.put("caption", mPost.caption!!)
                soldHashMap.put("timestamp", timestamp)
                soldHashMap.put("imageCount", mPost.imageCount!!)
                soldHashMap.put("category1", mPost.category1!!)
                soldHashMap.put("category2", mPost.category2!!)
                soldHashMap.put("status", mPost.status!!)
                soldHashMap.put("shipPayer", mPost.shipPayer!!)
                soldHashMap.put("shipWay", mPost.shipWay!!)
                soldHashMap.put("shipDeadLine", mPost.shipDeadLine!!)
                soldHashMap.put("shippedDateTimestamp", mPost.shippedDateTimestamp!!)
                soldHashMap.put("shipFrom", mPost.shipFrom!!)
                soldHashMap.put("price", mPost.price!!)
                soldHashMap.put("transactionStatus", "sold")
                soldHashMap.put("sellerShouldDo", soldFinish)
                soldHashMap.put("purchaserUid", mPost.purchaserUid!!)

                soldHashMap.put("image0", mPost.imageUrls[0])
                soldHashMap.put("ratio0", mPost.ratios[0])
                soldHashMap.put("image1", mPost.imageUrls[1])
                soldHashMap.put("ratio1", mPost.ratios[1])
                soldHashMap.put("image2", mPost.imageUrls[2])
                soldHashMap.put("ratio2", mPost.ratios[2])

                mPost.likes?.let {
                    soldHashMap.put("likes", it)
                }
                mPost.comments?.let {
                    soldHashMap.put("comments", it)
                }
                mPost.likeCount?.let {
                    soldHashMap.put("likeCount", it)
                }
                mPost.commentCount?.let {
                    soldHashMap.put("commentCount", it)
                }
                mPost.brand?.let {
                    soldHashMap.put("brand", mPost.brand!!)

                }
            }

            3 -> {
                soldHashMap.put("thumbnail", mPost.thumbnailUrl!!)
                soldHashMap.put("thumbnailRatio", mPost.thumbnailRatio!!)
                soldHashMap.put("commisionRate", mPost.commisionRate!!)
                soldHashMap.put("uid", currentUid!!)
                soldHashMap.put("title", mPost.title!!)
                soldHashMap.put("caption", mPost.caption!!)
                soldHashMap.put("timestamp", timestamp)
                soldHashMap.put("imageCount", mPost.imageCount!!)
                soldHashMap.put("category1", mPost.category1!!)
                soldHashMap.put("category2", mPost.category2!!)
                soldHashMap.put("status", mPost.status!!)
                soldHashMap.put("shipPayer", mPost.shipPayer!!)
                soldHashMap.put("shipWay", mPost.shipWay!!)
                soldHashMap.put("shipDeadLine", mPost.shipDeadLine!!)
                soldHashMap.put("shippedDateTimestamp", mPost.shippedDateTimestamp!!)
                soldHashMap.put("shipFrom", mPost.shipFrom!!)
                soldHashMap.put("price", mPost.price!!)
                soldHashMap.put("transactionStatus", "sold")
                soldHashMap.put("sellerShouldDo", soldFinish)
                soldHashMap.put("purchaserUid", mPost.purchaserUid!!)

                soldHashMap.put("image0", mPost.imageUrls[0])
                soldHashMap.put("ratio0", mPost.ratios[0])
                soldHashMap.put("image1", mPost.imageUrls[1])
                soldHashMap.put("ratio1", mPost.ratios[1])
                soldHashMap.put("image2", mPost.imageUrls[2])
                soldHashMap.put("ratio2", mPost.ratios[2])
                soldHashMap.put("image3", mPost.imageUrls[3])
                soldHashMap.put("ratio3", mPost.ratios[3])

                mPost.likes?.let {
                    soldHashMap.put("likes", it)
                }
                mPost.comments?.let {
                    soldHashMap.put("comments", it)
                }
                mPost.likeCount?.let {
                    soldHashMap.put("likeCount", it)
                }
                mPost.commentCount?.let {
                    soldHashMap.put("commentCount", it)
                }
                mPost.brand?.let {
                    soldHashMap.put("brand", mPost.brand!!)

                }
            }
        }

        Api.SoldOut.REF_SOLDOUT_POSTS.child(mPost.id!!).setValue(soldHashMap)
        Api.SoldOut.REF_MY_SOLDOUT_POSTS.child(currentUid!!).child(mPost.id!!).child("timestamp").setValue(timestamp)

        val commision = (mPost.price!!.toFloat() * mPost.commisionRate!!).toInt()

        val totalAmount = mPost.price!! - commision
        val key = Api.Charge.REF_MYCHARGE.child(currentUid).push().key

        var chargeHashMap = HashMap<String, Any>()
        chargeHashMap.put("postId", mPost.id!!)
        chargeHashMap.put("timestamp", timestamp)
        chargeHashMap.put("title", mPost.title!!)
        chargeHashMap.put("price", totalAmount)
        chargeHashMap.put("type", "sold")

        Api.Charge.REF_MYCHARGE.child(currentUid!!).child(key).setValue(chargeHashMap)


        var result2 = HashMap<String, Any>()
        result2.put(getString(R.string.sellerShouldDo), soldFinish)
        result2.put(getString(R.string.purchaserShouldDo), buyFinish)
        result2.put(getString(R.string.transactionStatus), "sold")


        Api.Post.REF_POSTS.child(mPost.id!!).updateChildren(result2, DatabaseReference.CompletionListener { databaseError, databaseReference ->

            valueBtn.isEnabled = true

            if (databaseError != null) {

                makeToast(this@SellNavi3Activity, databaseError.toString())
                return@CompletionListener
            }


            Api.MySellPosts.REF_MYSELL.child(currentUid!!).child(mPost.id!!)
                .child(getString(R.string.timestamp)).setValue(timestamp)

            Api.MyPurchasePosts.REF_MYPURCHASE.child(mPost.purchaserUid!!).child(mPost.id!!).child(getString(R.string.timestamp)).setValue(
                timestamp)


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

    fun sendPushNotification(token: String) {

        //表示タイトルの文字数の限界を設定
        val indexNum = pushNotiTitleCount
        var titleStr: String = mPost.title!!

        if (titleStr.count() > indexNum) {
            titleStr = titleStr.substring(0, indexNum)
            titleStr = titleStr+"..."
        }
        val message = "「" + titleStr + "」の取引の評価がつきました。これで取引は完了です。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        },{

            //onError


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
