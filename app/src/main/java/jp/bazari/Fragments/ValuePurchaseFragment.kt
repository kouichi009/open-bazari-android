package jp.bazari.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.bazari.Adapters.ValueItemAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import jp.bazari.models.UserModel
import jp.bazari.models.ValueModel
import kotlinx.android.synthetic.main.fragment_sell_list.view.*


class ValuePurchaseFragment : Fragment() {

    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_value_all, container, false)
        setHasOptionsMenu(true)

        view.recyclerView.layoutManager = LinearLayoutManager(mContext)

        var valuemodels: ArrayList<ValueModel>? = null
        var usermodels: ArrayList<UserModel>? = null
        arguments?.let {
            valuemodels = it.getSerializable(IntentKey.VALUES.name) as ArrayList<ValueModel>?
            usermodels = it.getSerializable(IntentKey.USERMODELS.name) as ArrayList<UserModel>?
        }

        view.recyclerView.adapter = ValueItemAdapter(valuemodels, usermodels, mContext)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {

        @JvmStatic
        fun newInstance(valuemodels: ArrayList<ValueModel>?, usermodels: ArrayList<UserModel>?) =
            ValuePurchaseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(IntentKey.VALUES.name, valuemodels)
                    putSerializable(IntentKey.USERMODELS.name, usermodels)
                }
            }
    }
}
