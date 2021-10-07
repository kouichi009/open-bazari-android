package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        optionTv.text = "設定"
        backArrow.setOnClickListener {
            finish()
        }

        addressLayout.setOnClickListener {
            val intent = Intent(this, Name_AddressActivity::class.java)
            startActivity(intent)
        }

        creditCardLayout.setOnClickListener {

            val intent = Intent(this, DisplayCardActivity::class.java).apply {
                putExtra(IntentKey.WHICHACTIVITYTODISTINGUISH.name, "SettingActivity")
            }
            startActivity(intent)
        }
    }
}
