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
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.post_list_content.view.*

class Liking_Commenting_PostAdapter
    (private val mPostList: ArrayList<Post>?, private val mContext: Context?) :
    RecyclerView.Adapter<Liking_Commenting_PostViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mPostList?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Liking_Commenting_PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.like_comment_list_content, parent, false)

//        cellForRow.shouldDoTv.setBackgroundResource(R.drawable.red_circle_flame_style)
        return Liking_Commenting_PostViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: Liking_Commenting_PostViewHolder, position: Int) {
        val post = mPostList!!.get(position)
        Picasso.get()
            .load(Uri.parse(post.thumbnailUrl))
            .into(holder.itemView?.postIv)
        holder.itemView?.titleTv?.text = post.title
        holder.itemView?.priceTv?.text = post.price?.toString() + "円"

        holder.itemView?.setOnClickListener {

            try {

                val post1 = mPostList.get(position)
                val a = post1.title
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

class Liking_Commenting_PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}