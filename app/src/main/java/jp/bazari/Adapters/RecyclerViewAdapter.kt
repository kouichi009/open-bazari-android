package jp.bazari.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jp.bazari.Activities.DetailActivity
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.getFormatPrice
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.grid_item.view.*


class RecyclerViewAdapter
    (private val mPostList: ArrayList<Post>?, private val mContext: Context?) :
    RecyclerView.Adapter<GridViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mPostList?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.grid_item, parent, false)

//        cellForRow.shouldDoTv.setBackgroundResource(R.drawable.red_circle_flame_style)
        return GridViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val post = mPostList!!.get(position)
        Picasso.get()
            .load(Uri.parse(post.thumbnailUrl))
            .into(holder.itemView?.postIv)
        holder.itemView?.titleTv?.text = post.title
        holder.itemView?.priceTv?.text = getFormatPrice(post.price!!)+"円"

        if (post.transactionStatus == mContext?.getString(R.string.transaction) ||
                post.transactionStatus == mContext?.getString(R.string.sold)) {

            holder.itemView?.soldOutImage?.visibility = View.VISIBLE
        } else {
            holder.itemView?.soldOutImage?.visibility = View.INVISIBLE
        }

        holder.itemView?.setOnClickListener {

            try {

                val post1 = mPostList.get(position)
                val a = post1.title
                val b = post1.id
                val intent = Intent(mContext, DetailActivity::class.java).apply {
                    putExtra(IntentKey.POST_ID.name, mPostList.get(position).id)
                }
                mContext?.startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}