package jp.bazari.Adapters

import android.content.Context
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import jp.bazari.Apis.Api
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Apis.getDateFromTimestamp
import jp.bazari.Apis.makeToast
import jp.bazari.R
import jp.bazari.models.Comment
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.comment_list_content.view.*
import java.util.*

class CommentsAdapter
    (
    private val mContext: Context?, private val layout_id: Int,
    private val mComments: ArrayList<Comment>?, private val mUsermodels: ArrayList<UserModel>?, private val mPost: Post
) :
    ArrayAdapter<Comment>(mContext, layout_id, mComments) {


    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getItem(position: Int): Comment {
        return super.getItem(position)
    }

    override fun getPosition(item: Comment?): Int {
        return super.getPosition(item)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView

        if (view == null) {
            val li = LayoutInflater.from(mContext)
            view = li.inflate(R.layout.comment_list_content, null)
        }
        val comment = getItem(position)

        view!!.usernameTv.text = mUsermodels!![position].username
        Picasso.get()
            .load(Uri.parse(mUsermodels!![position].profileImageUrl))
            .into(view!!.profileIv)
        view!!.commentTv.text = comment.commentText!!
        view!!.timeTv.text = getDateFromTimestamp(comment.timestamp!!)

        view!!.moreBtn.setOnClickListener {
            // commentsActivity!!.commentAction(position)

            if (getCurrentUser() == null) {
                return@setOnClickListener
            }

            val currentUid = getCurrentUser()!!.uid

            val popupMenu = PopupMenu(mContext!!, view!!.moreBtn)

            if (mComments!![position].uid == currentUid) {
                popupMenu.inflate(R.menu.option_menu_delete)

            } else if (mPost.uid == currentUid) {
                popupMenu.inflate(R.menu.option_menu_complain_delete)

            } else if (mComments!![position].uid != currentUid) {
                popupMenu.inflate(R.menu.option_menu_complain)
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_complain -> {
                        showAlertDialog(mContext?.getString(R.string.complain), position)

                    }
                    R.id.menu_item_delete -> {
                        //Delete item

                        showAlertDialog(mContext?.getString(R.string.delete), position)

                    }
                }
                false
            }
            popupMenu.show()
        }
        return view!!
    }

    fun showAlertDialog(str: String, position: Int) {

        val builder = AlertDialog.Builder(mContext!!)
        builder.setTitle(str)

        if (str == mContext.getString(R.string.complain)) {
            builder.setMessage("このコメントを通報していいですか？")
        } else if (str == mContext.getString(R.string.delete)) {
            builder.setMessage("このコメントを削除していいですか？")
        }

        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()

            if (str == mContext.getString(R.string.complain)) {
                makeToast(mContext!!, "通報しました")
                val autoKey = Api.Complain.REF_COMPLAINS.child(getCurrentUser()!!.uid).push().key

                var result = HashMap<String, Any>()
                result.put("type", "comment")
                result.put("to", mComments!![position].uid!!)
                result.put("commentText", mComments!![position].commentText!!)
                result.put("from", getCurrentUser()!!.uid)
                result.put("id", mComments!![position].id!!)

                Api.Complain.REF_COMPLAINS.child(getCurrentUser()!!.uid).child(autoKey).setValue(result)

            } else if (str == mContext.getString(R.string.delete)) {
                makeToast(mContext!!, "削除しました")

                val commentCount = mComments!!.size - 1
                Api.Comment.REF_POST_COMMENTS.child(mComments!![position].postId).child(mComments!![position].id)
                    .removeValue()
                Api.Post.REF_POSTS.child(mComments!![position].postId).child("comments")
                    .child(mComments!![position].uid).removeValue()

                var result = HashMap<String, Any>()
                result.put("commentCount", commentCount)
                Api.Post.REF_POSTS.child(mComments!![position].postId).updateChildren(result)

                Api.Comment.REF_COMMENTS.child(mComments!![position].id).removeValue()

                mComments?.removeAt(position)
                mUsermodels?.removeAt(position)
                notifyDataSetChanged()
            }
        }


        builder.setNegativeButton("キャンセル") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

}