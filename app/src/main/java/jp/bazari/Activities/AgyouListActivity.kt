package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_agyou_list.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class AgyouListActivity : AppCompatActivity() {

    val gojuon = arrayListOf<ArrayList<String>>(
        arrayListOf<String>("あ","い","う","え","お"),
        arrayListOf<String>("か","き","く","け","こ"),
        arrayListOf<String>("さ","し","す","せ","そ"),
        arrayListOf<String>("た","ち","つ","て","と"),
        arrayListOf<String>("な","に","ぬ","ね","の"),
        arrayListOf<String>("は","ひ","ふ","へ","ほ"),
        arrayListOf<String>("ま","み","む","め","も"),
        arrayListOf<String>("や","ゆ","よ"),
        arrayListOf<String>("ら","り","る","れ","ろ"),
        arrayListOf<String>("わ")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agyou_list)

        val bundle = intent.extras
        val str = bundle.getString(IntentKey.GOJUON1.name)

        backArrow.setOnClickListener {
            finish()
        }

        lateinit var selectArray: ArrayList<String>

        if (str == "あ行") {
            selectArray = gojuon[0]

        } else if (str == "か行") {
            selectArray = gojuon[1]

        } else if (str == "さ行") {
            selectArray = gojuon[2]

        } else if (str == "た行") {
            selectArray = gojuon[3]

        } else if (str == "な行") {
            selectArray = gojuon[4]

        } else if (str == "は行") {
            selectArray = gojuon[5]

        } else if (str == "ま行") {
            selectArray = gojuon[6]

        } else if (str == "や行") {
            selectArray = gojuon[7]

        } else if (str == "ら行") {
            selectArray = gojuon[8]

        } else if (str == "わ行") {
            selectArray = gojuon[9]

        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectArray)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            lateinit var selectStr: String

            if (str == "あ行") {
                selectStr = gojuon[0][position]

            } else if (str == "か行") {
                selectStr = gojuon[1][position]

            } else if (str == "さ行") {
                selectStr = gojuon[2][position]

            } else if (str == "た行") {
                selectStr = gojuon[3][position]

            } else if (str == "な行") {
                selectStr = gojuon[4][position]

            } else if (str == "は行") {
                selectStr = gojuon[5][position]

            } else if (str == "ま行") {
                selectStr = gojuon[6][position]

            } else if (str == "や行") {
                selectStr = gojuon[7][position]

            } else if (str == "ら行") {
                selectStr = gojuon[8][position]

            } else if (str == "わ行") {
                selectStr = gojuon[9][position]

            }

            val intent = Intent(this, BanksNameListActivity::class.java).apply {
                putExtra(IntentKey.GOJUON2.name, selectStr)
            }
            startActivity(intent)
        }
    }
}
