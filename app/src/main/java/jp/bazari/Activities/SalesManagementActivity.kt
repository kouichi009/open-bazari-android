package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Apis.Api
import jp.bazari.Apis.getCurrentUser
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_sales_management.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class SalesManagementActivity : AppCompatActivity() {

    var currentUid: String? = null
    var totalAmount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_management)

        trasferApplicationLayout.setOnClickListener {
            val intent = Intent(this@SalesManagementActivity, BankSelectActivity::class.java)
            startActivity(intent)
        }

        chargeHistoryLayout.setOnClickListener {
            val intent = Intent(this@SalesManagementActivity, ChargeActivity::class.java)
            startActivity(intent)
        }

        optionTv.text = "売上管理"
        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        loadCharge()
    }

    fun loadCharge() {

        currentUid = getCurrentUser()!!.uid

        salesBalanceTv.text = " loading..."
        totalAmount = 0

        var count = 0
        Api.Charge.observeMyCharges(currentUid!!, { charge, chargeCount ->

            if (chargeCount == 0) {
                salesBalanceTv.text = "0円"
                return@observeMyCharges
            }

            count++

            charge?.let {

                if (it.type == getString(R.string.sold)) {
                    totalAmount = totalAmount + it.price!!
                } else {
                    totalAmount = totalAmount - it.price!!
                }
            }

            if (count == chargeCount) {
                salesBalanceTv.text = totalAmount.toString() + "円"
            }
        })
    }
}
