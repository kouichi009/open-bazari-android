package jp.bazari.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Adapters.ChargeAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.getCurrentUser
import jp.bazari.R
import jp.bazari.models.Charge
import kotlinx.android.synthetic.main.activity_charge.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class ChargeActivity : AppCompatActivity() {

    lateinit var currentUid: String
    var charges = ArrayList<Charge>()
    var chargeAdapter: ChargeAdapter? = null
    var totalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)

        currentUid = getCurrentUser()!!.uid

        optionTv.text = "入出金・チャージ履歴"
        backArrow.setOnClickListener {
            finish()
        }

        loadCharge()
    }

    fun loadCharge() {

        var count = 0
        totalSalesAmount.text = " loading..."
        Api.Charge.observeMyCharges(currentUid, { charge, chargeCount ->

            if (chargeCount == 0) {
                totalSalesAmount.text = "0円"
                return@observeMyCharges
            }

            count++

            charge?.let {

                charges.add(0, it)

                if (it.type == getString(R.string.sold)) {
                    totalAmount = totalAmount + it.price!!
                } else {
                    totalAmount = totalAmount - it.price!!
                }
            }

            if (count == chargeCount) {

                totalSalesAmount.text = totalAmount.toString()
                chargeAdapter = ChargeAdapter(this, R.layout.charge_content, charges)
                listView.adapter = chargeAdapter
            }
        })
    }
}
