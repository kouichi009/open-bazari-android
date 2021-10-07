package jp.bazari.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.bazari.Adapters.PostItemAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.fragment_sell_list.view.*

class PurchaseListSoldOutFragment : Fragment() {

    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_purchase_list_sold_out, container, false)
        setHasOptionsMenu(true)

        view.recyclerView.layoutManager = LinearLayoutManager(mContext)

        var postList: ArrayList<Post>? = null
        arguments?.let {
            postList = it.getSerializable(IntentKey.POSTS.name) as ArrayList<Post>?
        }

        view.recyclerView.adapter = PostItemAdapter(postList, mContext, getString(R.string.PurchaseListActivity))

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {

        @JvmStatic
        fun newInstance(postList: ArrayList<Post>?) =
            PurchaseListSoldOutFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(IntentKey.POSTS.name, postList)
                }
            }
    }
}
