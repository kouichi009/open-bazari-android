package jp.bazari.Adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jp.bazari.Apis.Value
import jp.bazari.R
import jp.bazari.models.UserModel
import jp.bazari.models.ValueModel
import kotlinx.android.synthetic.main.value_list_content.view.*

class ValueItemAdapter
    (
    private val mValues: ArrayList<ValueModel>?,
    private val mUsermodels: ArrayList<UserModel>?,
    private val mContext: Context?
) :
    RecyclerView.Adapter<ValueItemViewHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {

        mValues?.let {
            return it.count()
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValueItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.value_list_content, parent, false)

//        cellForRow.shouldDoTv.setBackgroundResource(R.drawable.red_circle_flame_style)
        return ValueItemViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: ValueItemViewHolder, position: Int) {
        val valuemodel = mValues!!.get(position)

        var usermodel: UserModel? = null
        mUsermodels?.forEach {
            if (valuemodel.from == it.id) {
                usermodel = it
            }
        }

        usermodel?.let {
            Picasso.get()
                .load(Uri.parse(it.profileImageUrl))
                .into(holder.itemView?.profileIv)

            holder.itemView?.usernameTv?.text = it.username
        }


        if (valuemodel.valueStatus == Value.sun.toString()) {
            holder.itemView?.statusIv?.setImageResource(R.drawable.sun)
        }

        holder.itemView?.statusTv?.text = valuemodel.valueStatus
    }
}

class ValueItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}