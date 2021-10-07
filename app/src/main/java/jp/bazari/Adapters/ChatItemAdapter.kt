package jp.bazari.Adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jp.bazari.R
import jp.bazari.models.Chat
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.chat_list_content.view.*
import java.util.*
class ChatItemAdapter
    (
    private val mContext: Context?,
    private val mChats: ArrayList<Chat>?, private val mUsermodel: UserModel?,
    private val mCurrentUserModel: UserModel?
) :  RecyclerView.Adapter<ChatItemViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mChats?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.chat_list_content, parent, false)

        return ChatItemViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        val chat = mChats!!.get(position)
        var usermodel: UserModel? = null
        if (chat.uid == mUsermodel?.id) {
            usermodel = mUsermodel
        } else if (chat.uid == mCurrentUserModel?.id) {
            usermodel = mCurrentUserModel
        }

        usermodel?.let {
            Picasso.get()
                .load(Uri.parse(it.profileImageUrl))
                .into(holder.itemView?.profileIv)

            holder.itemView?.usernameTv?.text = it.username
        }

        holder.itemView?.chatTv?.text = chat.messageText
        holder.itemView?.timeTv?.text = chat.date
    }


}

class ChatItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

}