package jp.bazari.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.bazari.Adapters.NotificationAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import jp.bazari.models.Notification
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.fragment_sell_list.view.*

class NotificationFragment : Fragment() {

    var mContext: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_sell_list, container, false)
        setHasOptionsMenu(true)

        view.recyclerView.layoutManager = LinearLayoutManager(mContext)

        var notifications: ArrayList<Notification>? = null
        var posts: ArrayList<Post>? = null
        var usermodels: ArrayList<UserModel>? = null
        arguments?.let {
            notifications = it.getSerializable(IntentKey.NOTIFICATIONS.name) as ArrayList<Notification>?
            posts = it.getSerializable(IntentKey.POSTS.name) as ArrayList<Post>?
            usermodels = it.getSerializable(IntentKey.USERMODELS.name) as ArrayList<UserModel>

        }
        view.recyclerView.adapter = NotificationAdapter(notifications, posts, usermodels, mContext)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {

        @JvmStatic
        fun newInstance(notifications: ArrayList<Notification>?, mPosts: ArrayList<Post>?, mUserModels: ArrayList<UserModel>?) =
            NotificationFragment().apply {
                arguments = Bundle().apply {

                    putSerializable(IntentKey.NOTIFICATIONS.name, notifications)
                    putSerializable(IntentKey.POSTS.name, mPosts)
                    putSerializable(IntentKey.USERMODELS.name, mUserModels)
                }
            }
    }
}