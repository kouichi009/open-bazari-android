package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.shipPayerList
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_ship_payer_select.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class ShipPayerSelectActivity : AppCompatActivity() {

    val defaultSubtitle = arrayListOf<String>("送料を含めるため、購入者にとって親切です", "購入者が受け取りの時に、送料を支払います。")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ship_payer_select)

        tv1.text = shipPayerList[0]
        tv3.text = defaultSubtitle[0]
        tv4.text = shipPayerList[1]
        tv5.text = defaultSubtitle[1]

        sellerPayLayout.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IntentKey.SHIPPAYER.name, shipPayerList[0])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        purchaserPayLayout.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IntentKey.SHIPPAYER.name, shipPayerList[1])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

}
