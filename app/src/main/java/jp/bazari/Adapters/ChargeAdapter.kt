package jp.bazari.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.bazari.Apis.getDateFromTimestamp
import jp.bazari.R
import jp.bazari.models.Charge
import kotlinx.android.synthetic.main.charge_content.view.*
import java.util.*

class ChargeAdapter(private val mContext: Context?, private val layout_id: Int, private val mChargeList: ArrayList<Charge>?) :
    ArrayAdapter<Charge>(mContext, layout_id, mChargeList) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getItem(position: Int): Charge {
        return super.getItem(position)
    }

    override fun getPosition(item: Charge?): Int {
        return super.getPosition(item)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView

        if (view == null) {
            val li = LayoutInflater.from(mContext)
            view = li.inflate(R.layout.charge_content, null)
        }
        val charge = getItem(position)

        if (charge.type == mContext?.getString(R.string.sold)) {
            view!!.statusTv.text = "売却済"
            view!!.priceTv.text = charge.price!!.toString()+"円"
            view!!.priceTv.setTextColor(Color.BLACK)

        } else if (charge.type == mContext?.getString(R.string.application)) {
            view!!.statusTv.text = "申請済"
            view!!.priceTv.text = "-"+charge.price!!.toString()+"円"
            view!!.priceTv.setTextColor(Color.RED)

        }
        view!!.titleTv.text = charge.titleStr
        view!!.dateTv.text = getDateFromTimestamp(charge.timestamp!!)

        return view!!
    }
}