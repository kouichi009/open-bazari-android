package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.shipDatesList
import jp.bazari.Apis.shipPayerList
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_ship_info_select.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class ShipInfoSelectActivity : AppCompatActivity() {

    val shipWays0 = arrayListOf<String>(
        "レターパックライト",
        "レターパックプラス",
        "クリックポスト",
        "宅急便コンパクト",
        "ゆうパック元払い",
        "ヤマト宅急便",
        "ゆうパケット",
        "ゆうメール元払い",
        "スマートレター",
        "普通郵便",
        "未定"
    )

    val shipWays1 = arrayListOf<String>("ゆうパック着払い", "ヤマト宅急便", "ゆうパケット", "ゆうメール着払い", "未定")

    val shipFromPrefectures = arrayListOf<String>(
        "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
        "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県",
        "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県",
        "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県",
        "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県",
        "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
    )

    var shipInfos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ship_info_select)

        val bundle = intent.extras
        val type = bundle.getString(IntentKey.SHIPINFOSELECTION.name)

        when (type) {

            getString(R.string.shipway) -> {

                val shipPayer = bundle.getString(IntentKey.SHIPPAYERSELECTED.name)
                if (shipPayer.equals(shipPayerList[0])) {
                    shipInfos = shipWays0
                } else if (shipPayer.equals(shipPayerList[1])) {
                    shipInfos = shipWays1
                }
            }

            getString(R.string.shipdeadline) ->
                shipInfos = shipDatesList


            getString(R.string.prefecture) ->
                shipInfos = shipFromPrefectures

        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shipInfos)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra(IntentKey.SHIPINFO.name, shipInfos[position])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }
}
