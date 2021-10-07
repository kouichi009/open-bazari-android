package jp.bazari.Activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import com.squareup.picasso.Picasso
import io.repro.android.Repro
import io.repro.android.tracking.PurchaseProperties
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.StripeUtils.StripeUtil
import jp.bazari.models.CardModel
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_purchase.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*



var purchaseActivity: PurchaseActivity? = null

class PurchaseActivity : AppCompatActivity() {

    private val TAG = "PurchaseActivity"

    lateinit var mPost: Post

    lateinit var currentUid: String

    lateinit var mUsermodel: UserModel

    lateinit var uidEmail: String

    var selectedCardModel: CardModel? = null
    var selectedCardId: String? = null

    var customerId: String? = null

    var cards: JSONArray = JSONArray()

    var last4NumFromStripeStrs = ArrayList<String>()

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        mSVProgressHUD = SVProgressHUD(this)

        purchaseActivity = this

        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post

        backArrow.setOnClickListener {
            finish()
        }

        getCurrentUser()?.let {

            currentUid = it.uid
            uidEmail = it.uid + "@gmail.com"
        }


    }

    fun initialize() {
        selectedCardModel = null
        selectedCardId = null
        customerId = null
        cards = JSONArray()
        last4NumFromStripeStrs = ArrayList<String>()
        cardNumTv.text = "-"
        payBtn.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        initialize()

        var count = 0
        Api.Card.observeCard(currentUid, { card, cardCount ->

            if (cardCount == 0) {
                //クレジットカード未登録
                cardNumTv.text = "未登録"
                cardNumTv.setTextColor(Color.parseColor("#555555"))
                payBtn.isEnabled = true
                return@observeCard
            }

            count++

            card?.let {

                if (it.deletedFlg == null) {
                    selectedCardModel = it
                }
            }

            if (count == cardCount) {

                payBtn.isEnabled = true
                fetchCardFromStripe(uidEmail)
            }
        })

        payBtn.setOnClickListener {
            purchase()
        }

        cardLayout.setOnClickListener {
            goToDisplayActivity()
        }

        observeInfo()
    }

    fun goToDisplayActivity() {
        val intent = Intent(this@PurchaseActivity, DisplayCardActivity::class.java).apply {
            putExtra(IntentKey.WHICHACTIVITYTODISTINGUISH.name, "PurchaseActivity")
        }
        startActivity(intent)
    }


    fun observeInfo() {

        Api.User.observeUser(mPost.uid!!, {

            it?.let {
                mUsermodel = it
            }
        })

        Api.Address.observeAddress(currentUid, { address ->

            address?.let {

                postalCodeTv.text = "〒" + it.postalCode

                var shipAddress = it.prefecure + it.city + it.tyou

                it.building?.let { building ->
                    shipAddress = shipAddress + building
                }

                addressTv.text = shipAddress
                nameTv.text = it.seiKanji + it.meiKanji


            }
        })


        Picasso.get()
            .load(Uri.parse(mPost.thumbnailUrl))
            .into(postIv)

        titleTv.text = mPost.title
        priceTv1.text = getFormatPrice(mPost.price!!)+"円"
        priceTv2.text = getFormatPrice(mPost.price!!)+"円"
        shipPayerTv.text = mPost.shipPayer
        shipWayTv.text = mPost.shipWay
        shipDeadLineTv.text = mPost.shipDeadLine

    }

    private fun purchase() {

        if (selectedCardModel == null) {
            IOSDialog.Builder(this)
                .setMessage("クレジットカードが未登録です。")
                .setPositiveButton(
                    "登録する"
                ) { dialog, which ->

                    goToDisplayActivity()
                }
                .setNegativeButton(
                    "キャンセル", null
                ).show()
            return
        }

        IOSDialog.Builder(this)
            .setMessage("このまま購入してよろしいですか？")
            .setPositiveButton(
                "購入"
            ) { dialog, which ->

                mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)

                AsyncTask.execute {
                    postToStripe()
                }
            }
            .setNegativeButton(
                "キャンセル", null
            ).show()


    }

    fun setNotifications() {
        val timestamp = getTimestamp()
        val newNotificationReference = Api.Notification.REF_NOTIFICATION
        val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost.uid!!)
        val newNotificationId = newNotificationReference.push().key

        var result = HashMap<String, Any>()
        result.put("checked", false)
        result.put("from", currentUid)
        result.put("objectId", mPost.id!!)
        result.put("type", Type.naviPurchase)
        result.put("timestamp", timestamp)
        result.put("to", mPost.uid!!)
        result.put("segmentType", "transaction")


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
        val message = "「" + titleStr + "の商品代金が支払われました。商品を発送してください。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        }, {

            //onError


        })

    }

    fun fetchCardFromStripe(uidEmail: String) {
        //check if the customerId exist
        StripeUtil.getUserList(this, uidEmail, { customers ->

            customers?.let {

                if (it.length() > 0) {
                    val customer = it.get(0) as JSONObject
                    customerId = customer.get("id") as String
                    StripeUtil.getCardsList(this, customerId!!, { cardsList ->

                        cardsList?.let {

                            cards = it
                            for (i in 0..(it.length() - 1)) {
                                val cardIdJson = it.get(i) as JSONObject
                                val cardId = cardIdJson.get("id") as String
                                getCardLast4NumberFromStripe(i, cardId)

                            }
                        }
                    })
                }
            }
        })
    }

    fun getCardLast4NumberFromStripe(i: Int, cardId: String) {

        val last4JsonObj = cards.get(i) as JSONObject
        val last4 = last4JsonObj.get("last4") as String

        val monthJsonObj = cards.get(i) as JSONObject
        val month = monthJsonObj.get("exp_month") as Int

        val yearJsonObj = cards.get(i) as JSONObject
        val year = yearJsonObj.get("exp_year") as Int

        var expirationMonth = month.toString()
        var expirationYear = year.toString()

        //文字数をカウント
        if (expirationMonth.length < 2) {
            expirationMonth = "0" + expirationMonth
        }

        if (expirationYear.length > 2) {
            expirationYear = expirationYear.substring(2, 4)
        }

        val last4Num_Dates = last4 + expirationMonth + expirationYear

        last4NumFromStripeStrs.add(last4Num_Dates)

        if (selectedCardModel == null) {
            cardNumTv.text = "未登録"
            cardNumTv.setTextColor(Color.parseColor("#555555"))
            return;
        }

        selectedCardModel?.let {
            val a = it.id
            if (it.id == last4Num_Dates) {
                selectedCardId = cardId

                val previos = last4Num_Dates.substring(0, 4)
                val atras = last4Num_Dates.substring(4, 8)

                val month = atras.substring(0, 2)
                val year = atras.substring(2, 4)
                cardNumTv.text = "************" + previos + " " + month + "/" + year
                cardNumTv.setTextColor(Color.parseColor("#000000"))

            }
        }
    }

    fun postToStripe() {

        val yenPrice = mPost.price!! / 100

        // url to your server
        val url = herokuForStripeServerUrl + "/payment.php"

        val stringRequest = object : StringRequest(Request.Method.POST, url,

            Response.Listener { response ->
                Log.d(TAG, response)
                setDatabase()
            },

            Response.ErrorListener { error ->
                Log.d("VolleyErrorHere", error.toString())
                mSVProgressHUD?.dismiss()
            }
        ) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {

                var result = HashMap<String, String>()
                result.put("stripeToken", selectedCardId!!)
                result.put("amount", yenPrice.toString())
                result.put("currency", "jpy")
                result.put("description", selectedCardId!!)
                result.put("customerId", customerId!!)

                return result
            }


        } // end of string Request

        stringRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
                Log.d("TAG", error.message)
                mSVProgressHUD?.dismiss()
            }
        })
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add<String>(stringRequest)

    }

    fun setDatabase() {

        Api.MyPurchasePosts.REF_MYPURCHASE.child(currentUid).child(mPost.id!!)
            .child("timestamp").setValue(getTimestamp())

        var result = HashMap<String, Any>()
        result.put(getString(R.string.commisionRate), commisionRate)
        result.put(getString(R.string.transactionStatus), TransactionStatus.transaction)
        result.put(getString(R.string.purchaserUid), getCurrentUser()!!.uid)
        result.put(getString(R.string.purchaseDateTimestamp), getTimestamp())
        result.put(getString(R.string.purchaserShouldDo), waitForShip)
        result.put(getString(R.string.sellerShouldDo), ship)

        Api.Post.REF_POSTS.child(mPost.id!!)
            .updateChildren(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                if (databaseError != null) {

                    mSVProgressHUD?.dismiss()
                    makeToast(this@PurchaseActivity, databaseError.toString())
                    return@CompletionListener
                }


                Api.MySellPosts.REF_MYSELL.child(mPost.uid!!).child(mPost.id!!)
                    .child("timestamp").setValue(getTimestamp())

                val fcmToken = mUsermodel.fcmToken
                fcmToken?.let {

                    AsyncTask.execute { sendPushNotification(it) }

                }


                mSVProgressHUD?.dismiss()
                setNotifications()

                detailActivity?.changePurchaseBtn()

                reproPurchase()

                IOSDialog.Builder(this)
                    .setMessage("支払い完了しました！")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        finish()
                    }.show()
            })
    }

    //購入が確定したら
    fun reproPurchase() {
        val properties = PurchaseProperties()
        properties.setContentName(mPost.title)
        properties.setContentCategory(mPost.category1+" < "+mPost.category2);
        properties.setNumItems(1);

        Repro.trackPurchase(mPost.id, mPost.price!!.toDouble(), "JPY", properties)
    }
}
