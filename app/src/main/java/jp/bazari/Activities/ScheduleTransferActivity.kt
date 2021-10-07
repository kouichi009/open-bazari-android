package jp.bazari.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Apis.getFormatPrice
import jp.bazari.Apis.maximumTransferApplyPrice
import jp.bazari.Apis.minimumTransferApplyPrice
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_schedule_transfer.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class ScheduleTransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_transfer)

        ruleTv.text = "振込申請で一月あたりに申請できる金額は、\n"+ getFormatPrice(minimumTransferApplyPrice)+"円〜"+
                getFormatPrice(maximumTransferApplyPrice)+"円です。"

        backArrow.setOnClickListener {
            finish()
        }
    }
}
