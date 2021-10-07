package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class PrefectureListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefecture_list)

        backArrow.setOnClickListener {
            finish()
        }

        val prefectureList = arrayListOf<String>("北海道","青森県","岩手県","宮城県","秋田県","山形県","福島県",
            "茨城県","栃木県","群馬県","埼玉県","千葉県","東京都","神奈川県",
            "新潟県","富山県","石川県","福井県","山梨県","長野県","岐阜県",
            "静岡県","愛知県","三重県","滋賀県","京都府","大阪府","兵庫県",
            "奈良県","和歌山県","鳥取県","島根県","岡山県","広島県","山口県",
            "徳島県","香川県","愛媛県","高知県","福岡県","佐賀県","長崎県",
            "熊本県","大分県","宮崎県","鹿児島県","沖縄県")

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prefectureList)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { parent, view, position, id ->

            val prefecture = prefectureList[position]
            // intentの作成
            val intent = Intent()
            intent.putExtra(IntentKey.PREFECTURE.name, prefecture)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
