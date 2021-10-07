package jp.bazari.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.bazari.R
import jp.bazari.models.Comment
import kotlinx.android.synthetic.main.comment_list_content.view.*

class CommentItemAdapter
    (private val mCommentList: ArrayList<Comment>?, private val mContext: Context?)
    : RecyclerView.Adapter<CommentItemViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mCommentList?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.comment_list_content, parent, false)

        return CommentItemViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        val comment = mCommentList!!.get(position)
//        Picasso.get()
//            .load(Uri.parse(post.thumbnailUrl))
//            .into(holder.itemView?.postIv)

        holder.itemView?.usernameTv?.text = "user13"
        holder.itemView?.commentTv?.text = comment.commentText!!



        holder
    }


}

class CommentItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

}