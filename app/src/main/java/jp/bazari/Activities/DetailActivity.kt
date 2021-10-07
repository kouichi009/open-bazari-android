package jp.bazari.Activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bigkoo.svprogresshud.SVProgressHUD
import com.ligl.android.widget.iosdialog.IOSDialog
import com.squareup.picasso.Picasso
import io.repro.android.Repro
import io.repro.android.tracking.InitiateCheckoutProperties
import io.repro.android.tracking.ViewContentProperties
import jp.bazari.Adapters.ImagesAdapter
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.snippet_top_more_toolbar.*
import java.util.*

var detailActivity: DetailActivity? = null
var detailPost: Post? = null

class DetailActivity : AppCompatActivity(), PurchaseActivityInterface {

    private val TAG = "DetailActivity"

    var mPost: Post? = null

    var currentUid: String? = null

    var currentUserModel: UserModel? = null

    var mSVProgressHUD: SVProgressHUD? = null


    override fun changePurchaseBtn() {
        purchaseBtn.text = getString(R.string.sold_out)
        purchaseBtn.setTextColor(Color.WHITE)
        purchaseBtn.setBackgroundColor(Color.GRAY)
        purchaseBtn.isEnabled = false
    }


    //エラーが発生することがある。。ProfileUserActivityからDetailActivityを開いたときにおきることがある。
    //ProfileUserActivityはPaginationせずに一気に全部のPostを表示するため、メモリをいっぱい使って、Memoryouterrorを起こす。
    //30PostぐらいまではOK100を越えるとエラーになる。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        getCurrentUser()?.let {
            currentUid = it.uid
        }

        mSVProgressHUD = SVProgressHUD(this)

        val bundle = intent.extras
        val postId = bundle.getString(IntentKey.POST_ID.name)

        detailActivity = this

        purchaseBtn.isEnabled = false

        backArrow.setOnClickListener {
            finish()
        }

        Api.Post.observePost(postId, {

            it?.let {
                mPost = it
                detailPost = it
                //削除済みであれば、
                if (mPost!!.isCancel != null) {
                    finish()
                    makeToast(this, "すでに削除されたページです。")
                    return@observePost
                }

                if (it.uid == currentUid) {
                    purchaseBtn.visibility = View.GONE
                }

                Api.User.observeUser(it.uid!!, {

                    it?.let {
                        currentUserModel = it
                        setDetail()
                    }
                })

                priceTv.text = getFormatPrice(it.price!!)+ "円"

                if (it.shipPayer == shipPayerList[0]) {
                    ship_thing.text = "送料込み"
                } else {
                    ship_thing.text = ""
                }

                val a = mPost?.transactionStatus
                if (mPost?.transactionStatus == getString(R.string.transaction) ||
                    mPost?.transactionStatus == getString(R.string.sold)) {
                    purchaseBtn.text = getString(R.string.sold_out)
                    purchaseBtn.setTextColor(Color.WHITE)
                    purchaseBtn.setBackgroundColor(Color.GRAY)
                } else {
                    purchaseBtn.isEnabled = true
                }

                setPager()
                reproAnalyticsDetailPage()
            }
        })


        commentIv.setOnClickListener {

            if (currentUid == null) {
                goToRegsiterActivity(this)
                return@setOnClickListener
            }

            val intent = Intent(this@DetailActivity, CommentsActivity::class.java).apply {
                putExtra(IntentKey.POST.name, mPost)
            }

            startActivity(intent)
        }

        commentBtn.setOnClickListener {

            if (currentUid == null) {
                goToRegsiterActivity(this)
                return@setOnClickListener
            }

            val intent = Intent(this@DetailActivity, CommentsActivity::class.java).apply {
                putExtra(IntentKey.POST.name, mPost)
            }

            startActivity(intent)
        }


        purchaseBtn.setOnClickListener {

            if (currentUid == null) {

                goToRegsiterActivity(this)
                return@setOnClickListener
            }

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)
            Api.Address.observeAddress(currentUid!!, { address ->

                mSVProgressHUD?.dismiss()
                address?.let {

                    //住所が登録済みであれば
                    reproAnalyticsInitiateCheckout()
                    val intent = Intent(this, PurchaseActivity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPost)
                    }
                    startActivity(intent)
                    return@observeAddress
                }

                val intent = Intent(this, Name_AddressActivity::class.java).apply {
                    putExtra(IntentKey.POST.name, mPost)
                }
                startActivity(intent)
            })


        }

        heartLayout.setOnClickListener {

            if (currentUid == null) {
                goToRegsiterActivity(this)
                return@setOnClickListener
            }

            Api.Post.incrementLikes(postId!!, currentUid!!, {
                //success

                try {

                    if (it.isLiked == true) {
                        Api.MyLikePosts.REF_MYLIKING_POSTS.child(currentUid!!).child(postId)
                            .child(getString(R.string.timestamp)).setValue(
                                getTimestamp()
                            )

                        setNotificationDB()
                    } else {
                        Api.MyLikePosts.REF_MYLIKING_POSTS.child(currentUid!!).child(postId)
                            .child(getString(R.string.timestamp)).removeValue()

                    }

                    updateLike(it)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {
                //error
                makeToast(this, it!!)

            })
        }


        moreBtn.setOnClickListener {

            if (currentUid == null) {
                goToRegsiterActivity(this)
                return@setOnClickListener
            }

            pushMoreBtn()
        }

        profileLayout.setOnClickListener {
            val intent = Intent(this, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, mPost?.uid)
            }
            startActivity(intent)
        }
    }




    fun setDetail() {

        titleTv.text = mPost!!.title
        captionTv.text = mPost!!.caption
        category.text = mPost!!.category2
        if (mPost!!.brand == null) {
            brandTv.text = "メーカー名なし"
        } else {
            brandTv.text = mPost!!.brand
        }
        statusTv.text = mPost!!.status
        shipPayerTv.text = mPost!!.shipPayer
        shipWayTv.text = mPost!!.shipWay
        shipDeadLineTv.text = mPost!!.shipDeadLine
        shipFromTv.text = mPost!!.shipFrom

        Picasso.get()
            .load(Uri.parse(currentUserModel!!.profileImageUrl))
            .into(profileIv)

        usernameTv.text = currentUserModel!!.username

        currentUserModel!!.selfIntro?.let {
            self_introTv.text = it
        }

        if (currentUserModel!!.selfIntro == null) {
            self_introTv.text = ""
        }

        valueTv.text = currentUserModel!!.totalValue.toString()

        try {
            updateLike(mPost)
        } catch (e: java.lang.Exception) {
            e.stackTrace
        }
    }

    fun updateLike(post: Post?) {

        if (post?.isLiked == null || post?.isLiked == false) {
            image_heart.visibility = View.VISIBLE
            image_heart_red.visibility = View.INVISIBLE

        } else if (post?.isLiked!!) {
            image_heart.visibility = View.INVISIBLE
            image_heart_red.visibility = View.VISIBLE

        }

        post?.likeCount?.let {
            heartCountTv.text = it.toString()
        }

        post?.commentCount?.let {
            commentCountTv.text = it.toString()
        }

        if (post?.likeCount == null) {
            heartCountTv.text = ""
        }

        if (post?.commentCount == null) {
            commentCountTv.text = ""
        }


    }


    fun setNotificationDB() {

        Api.Notification.observeExistNotification(mPost!!.uid!!, mPost!!.id!!, "like", currentUid!!, {

            if (it == null) {
                val newNotificationReference = Api.Notification.REF_NOTIFICATION
                val newAutoKey = newNotificationReference.push().key
                val timestamp = getTimestamp()


                var result = HashMap<String, Any>()
                result.put("checked", false)
                result.put("from", currentUid!!)
                result.put("objectId", mPost!!.id!!)
                result.put("type", "like")
                result.put("timestamp", timestamp)
                result.put("to", mPost!!.uid!!)
                result.put("segmentType", "you")

                newNotificationReference.child(newAutoKey)


                val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost!!.uid!!)
                newMyNotificationReference.child(newAutoKey).child(getString(R.string.timestamp)).setValue(timestamp)

                Api.Notification.REF_EXIST_NOTIFICATION.child(mPost!!.uid!!).child(currentUid!!)
                    .child(mPost!!.id!!).child("like").child(newAutoKey).setValue(true)

                Api.User.observeUser(mPost!!.uid!!, {
                    val fcmToken = it?.fcmToken

                    fcmToken?.let {
                        AsyncTask.execute { sendPushNotification(it) }
                    }

                })
            }
        })
    }

    fun sendPushNotification(token: String) {

        //表示タイトルの文字数の限界を設定
        val indexNum = pushNotiTitleCount
        var titleStr: String = mPost!!.title!!

        if (titleStr.count() > indexNum) {
            titleStr = titleStr.substring(0, indexNum)
            titleStr = titleStr + "..."
        }
        val message = "「" + titleStr + "にいいねがつきました。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        }, {

            //onError


        })
    }

    fun setPager() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        viewPager.layoutParams.height = width

        val imagesAdapter = ImagesAdapter(supportFragmentManager)
        viewPager.adapter = imagesAdapter

        val dotsCount = imagesAdapter.count

        if (dotsCount <= 1) {
            return
        }
        //配列の初期化
        val dots = arrayOfNulls<ImageView>(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.nonactive_dot))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            sliderDotsLayout.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.active_dot))
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            override fun onPageSelected(position: Int) {

                for (i in 0 until dotsCount) {
                    dots[i]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.nonactive_dot))
                }

                dots[position]?.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.active_dot))
            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })
    }

    fun pushMoreBtn() {

        if (getCurrentUser() == null) {
            return
        }

        val popupMenu = PopupMenu(this, moreBtn)

        //取引中、soldoutであれば、通報するのみを表示させる。（削除をできなくすると、moreBtnを押しても何も表示されなくなってしまうため、通報するにする）
        if (mPost!!.transactionStatus == getString(R.string.transaction) || mPost!!.transactionStatus == getString(R.string.sold)) {
            popupMenu.inflate(R.menu.option_menu_complain)

        } else if (mPost!!.uid == currentUid!!) {
            popupMenu.inflate(R.menu.option_menu_delete)

        } else if (mPost!!.uid != currentUid!!) {
            popupMenu.inflate(R.menu.option_menu_complain)

        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_complain -> {
                    showAlertDialog(getString(R.string.complain))

                }
                R.id.menu_item_delete -> {
                    //Delete item

                    showAlertDialog(getString(R.string.delete))

                }
            }
            false
        }
        popupMenu.show()
    }

    fun showAlertDialog(str: String) {

        var message = String()

        if (str == this.getString(R.string.complain)) {
            message = "この投稿を通報していいですか？"
        } else if (str == this.getString(R.string.delete)) {
            message = "この投稿を削除していいですか？"
        }

        IOSDialog.Builder(this)
            .setTitle(str)
            .setMessage(message)
            // .setMessage("1")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->

                if (str == this.getString(R.string.complain)) {
                    makeToast(this, "通報しました")
                    val autoKey = Api.Complain.REF_COMPLAINS.child(currentUid!!).push().key

                    var result = java.util.HashMap<String, Any>()
                    result.put("type", "post")
                    result.put("to", mPost!!.uid!!)
                    result.put("title", mPost!!.title!!)
                    result.put("caption", mPost!!.caption!!)
                    result.put("id", mPost!!.id!!)
                    result.put("from", currentUid!!)

                    Api.Complain.REF_COMPLAINS.child(currentUid!!).child(autoKey).setValue(result)

                } else if (str == this.getString(R.string.delete)) {

                    Api.MySellPosts.REF_MYSELL.child(currentUid!!).child(mPost!!.id!!).removeValue()
                    Api.Comment.deleteComments(mPost!!.id!!)
                    Api.MyCommentPosts.deleteMyCommentingPost(mPost!!.id!!)
                    Api.MyLikePosts.deleteMyLikingPost(mPost!!.id!!)
                    Api.MyPosts.REF_MYPOSTS.child(currentUid!!).child(mPost!!.id!!).removeValue()
                    Api.Post.REF_POSTS.child(mPost!!.id!!).removeValue({ databaseError, databaseReference ->

                        makeToast(this, "削除しました。")
                        finish()
                    })
                }

            }
            .setNegativeButton(
                "キャンセル", null
            ).show()
    }

    fun reproAnalyticsDetailPage() {
        val properties = ViewContentProperties()

        mPost?.let { post->
            properties.value = post.price!!.toDouble()
            properties.currency = "JPY"
            properties.contentName = post.title
            properties.contentCategory = post.category2
            Repro.trackViewContent(post.id, properties)
        }
    }

    //支払い開始
    //ユーザーが「購入手続きへ」ボタンをタップしたとき
    fun reproAnalyticsInitiateCheckout() {

        mPost?.let {post ->
            val properties = InitiateCheckoutProperties()
            properties.setValue(post.price!!.toDouble())
            properties.setCurrency("JPY")
            properties.setContentName(post.title)
            properties.setContentCategory(post.category2)
            properties.setContentId(post.id)
            properties.setNumItems(1)

            Repro.trackInitiateCheckout(properties)
        }
    }

}
