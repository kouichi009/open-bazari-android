package jp.bazari.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jp.bazari.Activities.*
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.post_list_content.view.*

class PostItemAdapter
    (private val mPostList: ArrayList<Post>?, private val mContext: Context?, private val typeActivity: String?) :
    RecyclerView.Adapter<PostItemViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mPostList?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.post_list_content, parent, false)

        return PostItemViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        val post = mPostList!!.get(position)
        Picasso.get()
            .load(Uri.parse(post.thumbnailUrl))
            .into(holder.itemView?.postIv)
        holder.itemView?.titleTv?.text = post.title
        holder.itemView?.priceTv?.text = post.price?.toString() + "円"

        if (typeActivity == mContext!!.getString(R.string.PurchaseListActivity)) {
            holder.itemView?.shouldDoTv?.text = post.purchaserShouldDo

            when (post.purchaserShouldDo) {
                waitForShip -> {

                    holder.itemView?.shouldDoTv?.setTextColor(Color.RED)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_flame_style)
                }

                catchProduct -> {
                    holder.itemView?.shouldDoTv?.setTextColor(Color.WHITE)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_background_style)

                }

                waitForValue -> {
                    holder.itemView?.shouldDoTv?.setTextColor(Color.RED)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_flame_style)

                }

                buyFinish -> {
                    holder.itemView?.shouldDoTv?.setTextColor(Color.GRAY)

                }
            }
        } else {
            holder.itemView?.shouldDoTv?.text = post.sellerShouldDo

            when (post.sellerShouldDo) {
                ship -> {
                    holder.itemView?.shouldDoTv?.setTextColor(Color.WHITE)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_background_style)

                }

                waitCatch -> {

                    holder.itemView?.shouldDoTv?.setTextColor(Color.RED)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_flame_style)
                }

                valueBuyer -> {
                    holder.itemView?.shouldDoTv?.setTextColor(Color.WHITE)
                    holder.itemView?.shouldDoTv?.setBackgroundResource(R.drawable.red_circle_background_style)


                }

                soldFinish -> {

                    holder.itemView?.shouldDoTv?.setTextColor(Color.GRAY)
                }
            }
        }

        holder.itemView?.setOnClickListener {

            try {
                if (typeActivity == mContext!!.getString(R.string.PurchaseListActivity)) {

                    lateinit var intent: Intent

                    when (post.purchaserShouldDo) {
                        waitForShip -> {
                            intent = Intent(mContext, PurchaseNavi1Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                            }

                        }

                        catchProduct -> {
                            intent = Intent(mContext, PurchaseNavi2Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                                putExtra(IntentKey.FROMACTIVITY.name, mContext.getString(R.string.PurchaseListActivity))
                            }

                        }

                        waitForValue -> {
                            intent = Intent(mContext, PurchaseNavi3Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                            }

                        }

                        buyFinish -> {
                            intent = Intent(mContext, PurchaseNavi4Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                            }

                        }
                    }

                    mContext!!.startActivity(intent)

                } else if (typeActivity == mContext!!.getString(R.string.SellListActivity)) {

                    lateinit var intent: Intent

                    when (post.sellerShouldDo) {
                        ship -> {
                            intent = Intent(mContext, SellNavi1Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                                putExtra(IntentKey.FROMACTIVITY.name, mContext.getString(R.string.SellListActivity))
                            }

                        }

                        waitCatch -> {
                            intent = Intent(mContext, SellNavi2Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                            }

                        }

                        valueBuyer -> {
                            intent = Intent(mContext, SellNavi3Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                                putExtra(IntentKey.FROMACTIVITY.name, mContext.getString(R.string.SellListActivity))
                            }

                        }

                        soldFinish -> {
                            intent = Intent(mContext, SellNavi4Activity::class.java).apply {
                                putExtra(IntentKey.POST.name, mPostList.get(position))
                            }

                        }
                    }

                    mContext!!.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

class PostItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}