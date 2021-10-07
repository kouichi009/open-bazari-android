package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import jp.bazari.Apis.*
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_bank_select.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

var bankSelectActivity: BankSelectActivity? = null

class BankSelectActivity : AppCompatActivity(), SelectBankInterface {

    lateinit var currentUid: String
    var selectedBank: String? = null
    var selectedBankType: String? = null
    var shitenCodeNum: Long? = null
    var kouzaBangouNum: Long? = null
    var firstName: String? = null
    var lastName: String? = null

    var mSVProgressHUD: SVProgressHUD? = null

    val defaultBankSelect = "選択してください"

    override fun selectBank(bank: String) {

        bankSelectActivity = null
        showTex(bank)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_select)

        currentUid = getCurrentUser()!!.uid

        optionTv.text = "振込先口座の登録"
        backArrow.setOnClickListener {
            finish()
        }

        mSVProgressHUD = SVProgressHUD(this)

        bankNameTv.setText(defaultBankSelect)

        bankLayout.setOnClickListener {

            val intent = Intent(this, BanksListActivity::class.java)
            val requestCode = 1001
            bankSelectActivity = this
            startActivityForResult(intent, requestCode)
        }

        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.bankTypes, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        loadBankInfo()


        registerBtn.setOnClickListener {

            registerBankInfo()
        }
    }

    fun loadBankInfo() {

        Api.BankInfo.observeMyBank(currentUid, { bankInfo ->

            bankInfo?.let {

                selectedBank = bankInfo.bank
                selectedBankType = bankInfo.accountType
                shitenCodeNum = bankInfo.branchCode
                kouzaBangouNum = bankInfo.accountNumber
                firstName = bankInfo.firstName
                lastName = bankInfo.lastName
                selectedBankType = bankInfo.accountType
                setBankInfo()
            }
        })
    }

    fun setBankInfo(){

        var position = 0
        if (selectedBankType == "普通") {
            position = 0
        } else if (selectedBankType == "当座") {
            position = 1
        } else if (selectedBankType == "貯蓄") {
            position = 2
        }
        spinner.setSelection(position)
        accountCodeEdit.setText(shitenCodeNum.toString())
        accountNumberEdit.setText(kouzaBangouNum.toString())
        lastNameEdit.setText(lastName)
        firstNameEdit.setText(firstName)
        bankNameTv.text = selectedBank
    }

    fun loadCharge(completion: (Int) -> Unit){

        var totalAmount = 0
        var count = 0
        Api.Charge.observeMyCharges(currentUid, { charge, chargeCount ->

            if (chargeCount == 0) {
                completion(totalAmount)
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
                completion(totalAmount)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // startActivityForResult()の際に指定した識別コードとの比較
        if (requestCode == 1001) {

            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {

                // 返却されてきたintentから値を取り出す
                val bundle = data?.extras
                val str = bundle?.getString(IntentKey.BANKNAME.name)

                showTex(str!!)

            }
        }

    }

    fun showTex(bankName: String) {
        selectedBank = bankName
        bankNameTv.text = bankName
        bankNameTv.setTextColor(Color.BLACK)
    }

    fun registerBankInfo() {

        if (bankNameTv.text.toString() == defaultBankSelect) {
            showAlert("銀行を選択してください。")
            return
        }

        if (accountCodeEdit.text.toString().equals("")) {
            showAlert("3ケタの支店コードを入力してください。")
            return
        } else if (accountCodeEdit.text.toString().count() < 3) {
            showAlert("3ケタの支店コードを入力してください。")
            return
        }

        if (accountNumberEdit.text.toString().equals("")) {
            showAlert("7ケタの口座番号を入力してください。")
            return
        } else if (accountNumberEdit.text.toString().count() < 7) {
            showAlert("7ケタの口座番号を入力してください。")
            return
        }

        if (lastNameEdit.text.toString().equals("")) {
            showAlert("口座名義(セイ)を入力してください。")
            return
        }

        if (firstNameEdit.text.toString().equals("")) {
            showAlert("口座名義(メイ)を入力してください。")
            return
        }

        val bankName = bankNameTv.text
        val spinnerSelectItem = spinner.selectedItem as String
        val accountCode = accountCodeEdit.text.toString().toInt()
        val accountNumber = accountNumberEdit.text.toString().toInt()
        val lastName = lastNameEdit.text.toString()
        val firstName = firstNameEdit.text.toString()

        var result = HashMap<String, Any>()
        result.put("bank", bankName)
        result.put("accountType", spinnerSelectItem)
        result.put("branchCode", accountCode)
        result.put("accountNumber", accountNumber)
        result.put("lastName", lastName)
        result.put("firstName", firstName)

        Api.BankInfo.REF_MYBANK.child(currentUid).setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->
            if (databaseError != null) {
                makeToast(this, databaseError.message)
                return@CompletionListener
            }

            loadCharge { totalAmount ->

                val intent = Intent(this, TransferApplicationActivity::class.java).apply {
                    putExtra(IntentKey.TOTALAMOUNT.name, totalAmount)
                }

                startActivity(intent)
            }
        })
    }

    fun showAlert(message: String) {
        IOSDialog.Builder(this)
            .setPositiveButton("OK",null)
            .setMessage(message).show()

    }
}