package jp.bazari.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jp.bazari.Activities.*
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Notification
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.notification_list_content.view.*

class NotificationAdapter
    (
    private val mNotifications: ArrayList<Notification>?,
    private val mPosts: ArrayList<Post>?,
    private val mUserModels: ArrayList<UserModel>?,
    private val mContext: Context?
) :
    RecyclerView.Adapter<NotificationViewHolder>() {

    val NOTIFICATIONACTIVITY = "NotificationActivity"

    // numberOfItems
    override fun getItemCount(): Int {

        mNotifications?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.notification_list_content, parent, false)

        return NotificationViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = mNotifications!!.get(position)

        try {

            when (mNotifications[position].type) {

                "like" -> {

                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんが「" +
                            mPosts!![position].title!! + "」をいいね!しました。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)
                }

                "comment" -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんが「" +
                            mPosts!![position].title!! + "」にコメントしました。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)
                }

                "priceDown" -> {
                    holder.itemView?.titleTv?.text = "「" + mPosts!![position].title!! + "」が" + mPosts[position].price +
                            "円に値下げされました。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels!![position].profileImageUrl))
                        .into(holder.itemView?.profileIv)
                }

                Type.naviPurchase.toString() -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんから、「" +
                            mPosts!![position].title!! + "」の商品代金が支払われました。商品を発送してください。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)
                }

                Type.naviShip.toString() -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんから、「" +
                            mPosts!![position].title!! + "」が発送されました。商品が到着したら受取通知を行なってください。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)

                }

                Type.naviEvaluatePurchaser.toString() -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんから、「" +
                            mPosts!![position].title!! + "」の受取通知がありました。購入者の評価を行い、取引を完了させてください。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)

                }

                Type.naviEvaluateSeller.toString() -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんから取引の評価がつきました。これで取引は完了です。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)

                }

                Type.naviMessage.toString() -> {
                    holder.itemView?.titleTv?.text = mUserModels!![position].username + "さんから、「" +
                            mPosts!![position].title!! + "」の取引メッセージが届きました。"
                    Picasso.get()
                        .load(Uri.parse(mUserModels[position].profileImageUrl))
                        .into(holder.itemView?.profileIv)

                }

                "admin" -> {
                    holder.itemView?.titleTv?.text = mNotifications[position].objectId
                    Picasso.get()
                        .load(Uri.parse(adminImageURL))
                        .into(holder.itemView?.profileIv)
                }
            }


            holder.itemView?.timeTv?.text = getDateFromTimestamp(mNotifications[position].timestamp!!)

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


        holder.itemView?.setOnClickListener {

            try {

                if (mNotifications!![position].type == "comment" || mNotifications!![position].type == "like" ||
                    mNotifications!![position].type == "priceDown"
                ) {

                    val intent = Intent(mContext, DetailActivity::class.java).apply {
                        putExtra(IntentKey.POST_ID.name, mPosts!!.get(position).id)
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == ship && mNotifications!![position].type == Type.naviPurchase.toString()) {

                    val intent = Intent(mContext, SellNavi1Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == waitCatch && mNotifications[position].type == Type.naviPurchase.toString()) {
                    val intent = Intent(mContext, SellNavi2Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].purchaserShouldDo == catchProduct && mNotifications[position].type == Type.naviShip.toString()) {

                    val intent = Intent(mContext, PurchaseNavi2Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)

                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].purchaserShouldDo == waitForValue && mNotifications[position].type == Type.naviShip.toString()) {

                    val intent = Intent(mContext, PurchaseNavi3Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == valueBuyer && mNotifications[position].type == Type.naviEvaluatePurchaser.toString()) {

                    val intent = Intent(mContext, SellNavi3Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == valueBuyer && mNotifications[position].type == Type.naviPurchase.toString()) {
                    val intent = Intent(mContext, SellNavi3Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)
                    }
                    mContext!!.startActivity(intent)
                } else if (mPosts!![position].purchaserShouldDo == buyFinish && mNotifications[position].type == Type.naviEvaluateSeller.toString()) {

                    val intent = Intent(mContext, PurchaseNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)
                } else if (mPosts!![position].purchaserShouldDo == buyFinish && mNotifications[position].type == Type.naviShip.toString()) {

                    val intent = Intent(mContext, PurchaseNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)
                } else if (mPosts!![position].sellerShouldDo == soldFinish && mNotifications[position].type == Type.naviEvaluatePurchaser.toString()) {

                    val intent = Intent(mContext, SellNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == soldFinish && mNotifications[position].type == Type.naviPurchase.toString()) {

                    val intent = Intent(mContext, SellNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                else if (mPosts!![position].sellerShouldDo == ship && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid == mNotifications[position].to) {

                    val intent = Intent(mContext, SellNavi1Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)
                    }
                    mContext!!.startActivity(intent)
                } else if (mPosts!![position].sellerShouldDo == waitCatch && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid == mNotifications[position].to) {

                    val intent = Intent(mContext, SellNavi2Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].sellerShouldDo == valueBuyer && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid == mNotifications[position].to) {

                    val intent = Intent(mContext, SellNavi3Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)
                    }
                    mContext!!.startActivity(intent)
                } else if (mPosts!![position].sellerShouldDo == soldFinish && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid == mNotifications[position].to) {

                    val intent = Intent(mContext, SellNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                }

                /////////////////////////////////////////////////////////////////

                else if (mPosts!![position].purchaserShouldDo == waitForShip && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid != mNotifications[position].to) {

                    val intent = Intent(mContext, PurchaseNavi1Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].purchaserShouldDo == catchProduct && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid != mNotifications[position].to) {

                    val intent = Intent(mContext, PurchaseNavi2Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                        putExtra(IntentKey.FROMACTIVITY.name, NOTIFICATIONACTIVITY)

                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].purchaserShouldDo == waitForValue && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid != mNotifications[position].to) {

                    val intent = Intent(mContext, PurchaseNavi3Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                } else if (mPosts!![position].purchaserShouldDo == buyFinish && mNotifications[position].type == Type.naviMessage.toString() && mPosts!![position].uid != mNotifications[position].to) {

                    val intent = Intent(mContext, PurchaseNavi4Activity::class.java).apply {
                        putExtra(IntentKey.POST.name, mPosts.get(position))
                    }
                    mContext!!.startActivity(intent)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}