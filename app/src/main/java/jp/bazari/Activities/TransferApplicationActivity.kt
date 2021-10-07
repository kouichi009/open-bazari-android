package jp.bazari.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import jp.bazari.Apis.*
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_transfer_application.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*





class TransferApplicationActivity : AppCompatActivity() {

    lateinit var currentUid: String
    var totalAmount = 0
    var bankAccountNumber: Long? = null
    var bankAccountType: String? = null
    var bank: String? = null
    var branchCode: Long? = null
    var firstName: String? = null
    var lastName: String? = null

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_application)

        currentUid = getCurrentUser()!!.uid

        optionTv.text = "振込申請"
        backArrow.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        totalAmount = bundle.getInt(IntentKey.TOTALAMOUNT.name)

        totalAmountTv.text = totalAmount.toString()+"円"
        ruleTv.text = "・一度に振込申請できる金額は"+getFormatPrice(minimumTransferApplyPrice)+"円〜"+ getFormatPrice(
            maximumTransferApplyPrice)+"円です。\n・申請額が9,999円以下の場合、210円の振込手数料がかかります。"


        mSVProgressHUD = SVProgressHUD(this)

        Api.BankInfo.observeMyBank(currentUid, { bankInfo ->

            bankInfo?.let {
                bankAccountNumber = it.accountNumber
                bankAccountType = it.accountType
                bank = it.bank
                branchCode = it.branchCode
                firstName = it.firstName
                lastName = it.lastName
            }
        })

        bankTransBtn.setOnClickListener {

            pushTransferBtn()
        }

        scheduleLayout.setOnClickListener {
            val intent = Intent(this@TransferApplicationActivity, ScheduleTransferActivity::class.java)
            startActivity(intent)
        }

        val text = "・詳しいスケジュールはこちらをご覧ください。"

        val ss = SpannableString(text)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {

                val intent = Intent(this@TransferApplicationActivity, ScheduleTransferActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = true
            }
        }


        ss.setSpan(clickableSpan1, 11, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        detailScheduleTv.setText(ss)
        detailScheduleTv.setMovementMethod(LinkMovementMethod.getInstance())
    }

    fun pushTransferBtn() {

        if (applyPriceEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus("振込申請金額を入力してください。")
            return
        }

        if (applyPriceEdit.text.toString().substring(0,1).equals("0")) {
            mSVProgressHUD?.showErrorWithStatus("入力した値がおかしいです。")
            return
        }

        if (applyPriceEdit.text.toString().toInt() < minimumTransferApplyPrice) {
            mSVProgressHUD?.showErrorWithStatus("振込申請は"+ minimumTransferApplyPrice+"円以上からになります。")
            return
        }

        if (applyPriceEdit.text.toString().toInt() > maximumTransferApplyPrice) {
            mSVProgressHUD?.showErrorWithStatus("振込申請の上限は"+ maximumTransferApplyPrice+"円になります。")
            return
        }

        if (totalAmount < applyPriceEdit.text.toString().toInt()) {
            mSVProgressHUD?.showErrorWithStatus("残高不足です。")
            return
        }

        bankTransBtn.isEnabled = false

        IOSDialog.Builder(this)
            .setMessage("振込申請していいですか？")
            .setCancelable(false)
            .setPositiveButton(
                "はい"
            ) { dialog, which ->

                registerTransfer()

            }
            .setNegativeButton(
                "いいえ", { dialog, which ->
                    bankTransBtn.isEnabled = true
                }
            ).show()
    }

    fun registerTransfer() {

        mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.None)

        val inputPrice = applyPriceEdit.text.toString().toInt().toLong()
        val priceForCharge = inputPrice
        val today: String = /*"2018-12-12"*/ getHyphenDateFromTimestamp(System.currentTimeMillis()/1000)
        val dates = today.split("-")
        var year: String = dates[0]
        var yearLong = year.toLong()
        var month: String = dates[1]
        var monthLong = month.toLong()

        monthLong += 1

        if (monthLong > 12) {
            yearLong += 1
            monthLong = 1
            year = yearLong.toString()

        }

        month = monthLong.toString()
        val month_date = month + "-15"

        Api.BankTransDate.observeBankTrasDate(year, month_date, currentUid, { bankTrasDate ->

            // newPrice(bankTransferDatesノードのprice)
            // 今月分の申請が初回であれば、if let bankTransDateの中は読まずに、newPriceの金額がそのままノードにかかれる。
            var newPrice = priceForCharge

            //今月分の申請が2回目以降であれば、
            bankTrasDate?.let {

                //oldPrice(bankTransferDatesノードのpriceから引っ張って来たデータ)
                val oldPrice = it.price
                // 今回チャージする金額を上乗せて、newPriceに代入
                newPrice = oldPrice!! + priceForCharge

                if (newPrice > maximumTransferApplyPrice) {
                    showAlert("一月あたりの申請金額の上限を超えています。", "合計"+ getFormatPrice(maximumTransferApplyPrice)+"円を超える申請はできません。次月に再度申請してください。")

                    return@observeBankTrasDate
                }
            }

            //bankTransferDatesノード
            val ref = Api.BankTransDate.REF_BANK_TRANSFER_DATES.child(year).child(month_date).child(currentUid)

            var resultChild = HashMap<String, Any>()
            resultChild.put("accountNumber", bankAccountNumber!!)
            resultChild.put("accountType", bankAccountType!!)
            resultChild.put("bank", bank!!)
            resultChild.put("branchCode", branchCode!!)
            resultChild.put("firstName", firstName!!)
            resultChild.put("lastName", lastName!!)


            var result = HashMap<String, Any>()
            result.put("price", newPrice)
            result.put("applyDate", today)
            result.put("bank", resultChild)

            ref.setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    bankTransBtn.isEnabled = true
                    makeToast(this, databaseError.message)
                    return@CompletionListener
                }

                val autoKey = Api.Charge.REF_MYCHARGE.child(currentUid).push().key
                val timestamp = getTimestamp()

                var result2 = HashMap<String, Any>()
                result2.put("timestamp", timestamp)
                result2.put("title", "振込申請")
                result2.put("price", priceForCharge)
                result2.put("type", getString(R.string.application))

                Api.Charge.REF_MYCHARGE.child(currentUid).child(autoKey)
                    .setValue(result2, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                        if (databaseError != null) {
                            bankTransBtn.isEnabled = true
                            makeToast(this, databaseError.message)
                            return@CompletionListener
                        }

                        showAlert("", "振込申請完了")
                    })
            })
        })
    }

    fun showAlert(title: String, message: String) {
        mSVProgressHUD?.dismiss()

        IOSDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "OK"
            ) { dialog, which ->

                finish()

            }.show()
    }
}
