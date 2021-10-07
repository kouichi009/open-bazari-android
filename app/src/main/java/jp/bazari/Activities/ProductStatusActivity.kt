package jp.bazari.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.productStatuses
import jp.bazari.Apis.productSubtitles
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_product_status.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import kotlinx.android.synthetic.main.title_subtitle.view.*

class ProductStatusActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_status)

        backArrow.setOnClickListener {
            finish()
        }

        val adapter = MyCustomAdapter(this)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent()
            intent.putExtra(IntentKey.PRODUCT_STATUS.name, productStatuses[position])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private class MyCustomAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        init {
            mContext = context
        }

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return productStatuses.size
        }

        // you can also ignore this
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // you can ignore this for now
        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        // responsible for rendering out each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rootView = layoutInflater.inflate(R.layout.title_subtitle, viewGroup, false)

            rootView.titleTv.text = productStatuses.get(position)
            rootView.subTitleTv.text = productSubtitles.get(position)


            return rootView
//            val textView = TextView(mContext)
//            textView.text = "HERE is my ROW for my LISTVIEW"
//            return textView
        }
    }
}
