package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.categories
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_agyou_list.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class CategorySelectActivity : AppCompatActivity() {

    var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_select)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent()
            intent.putExtra(IntentKey.CATEGORY.name, categories.get(position))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }
}
