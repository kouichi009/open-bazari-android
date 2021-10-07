package jp.bazari.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import jp.bazari.R
import jp.bazari.models.BrandAll
import kotlinx.android.synthetic.main.item_list_subtitle.view.*
import java.util.*



class BrandSelectAdapter(context: Context, var list: ArrayList<BrandAll>) : BaseAdapter() {

    private val headers = arrayListOf<String>(" ","ア行","カ行","サ行","タ行","ナ行","ハ行","マ行","ヤ行","ラ行")

    private val inflater: LayoutInflater

    private val LIST_ITEM = 0
    private val HEADER = 1

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getItemViewType(position: Int): Int {

        val a = list[position]
        val b = a.enName
        val c = a.jpName

        when (list[position].enName) {
            " " -> return HEADER
            "ア行" -> return HEADER
            "カ行" -> return HEADER
            "サ行" -> return HEADER
            "タ行" -> return HEADER
            "ナ行" -> return HEADER
            "ハ行" -> return HEADER
            "マ行" -> return HEADER
            "ヤ行" -> return HEADER
            "ラ行" -> return HEADER
        }

        return LIST_ITEM

    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (convertView == null) {
            when (getItemViewType(position)) {
                LIST_ITEM -> {
                    convertView = inflater.inflate(R.layout.item_list_subtitle, null)
                }
                HEADER -> {
                    convertView = inflater.inflate(R.layout.item_list_header, null)
                }
            }
        }
        when (getItemViewType(position)) {
            LIST_ITEM -> {
                convertView!!.enTv.text = list[position].enName
                convertView!!.jpTv.text = list[position].jpName
            }

            HEADER -> {
                val title = convertView!!.findViewById<View>(R.id.itemListViewHeader) as TextView
                title.text = list[position].enName
            }
        }
        return convertView
    }

}
