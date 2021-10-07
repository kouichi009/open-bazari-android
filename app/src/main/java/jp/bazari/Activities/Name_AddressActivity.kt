package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import jp.bazari.Apis.Api
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Apis.makeToast
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_name__address.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class Name_AddressActivity : AppCompatActivity() {

    var seiKanji: String? = null
    var meiKanji: String? = null
    var seiKana: String? = null
    var meiKana: String? = null
    var phoneNumber: String? = null
    var postalCode: String? = null
    var prefecture: String? = null
    var city: String? = null
    var tyou: String? = null
    var currentUid: String? = null

    val defaultPrefectre = "選択してください。 ＞   "

    val nameList = arrayListOf<String>("姓（全角）", "名（全角）", "セイ（全角カナ）", "メイ（全角カナ）", "電話番号")
    val addressList = arrayListOf<String>("郵便番号","都道府県","市区町村","町名番地","建物名")

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name__address)

        currentUid = getCurrentUser()!!.uid

        optionTv.text = "連絡先・住所の設定"
        backArrow.setOnClickListener {
            finish()
        }

        mSVProgressHUD = SVProgressHUD(this)

        prefectureTv.text = defaultPrefectre
        alertLayout.visibility = View.GONE

        lastNameKanjiEdit.requestFocus()

        loadMyAddress()

        saveBtn.setOnClickListener {
           saveAddress()
        }

        prefectureLayout.setOnClickListener {

            val intent = Intent(this, PrefectureListActivity::class.java)
            val requestCode = 1001
            startActivityForResult(intent, requestCode)
        }
    }

    fun saveAddress() {

        if(lastNameKanjiEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(nameList.get(0)+"を入力してください。")
            return
        }

        if(firstNameKanjiEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(nameList.get(1)+"を入力してください。")
            return
        }

        if(lastNameEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(nameList.get(2)+"を入力してください。")
            return
        }

        if(firstNameEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(nameList.get(3)+"を入力してください。")
            return
        }

        if(phoneEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(nameList.get(4)+"を入力してください。")
            return
        }

        if(postalCodeEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(addressList.get(0)+"を入力してください。")
            return
        }

        if(prefectureTv.text.toString().equals(defaultPrefectre)) {
            mSVProgressHUD?.showErrorWithStatus(addressList.get(1)+"を入力してください。")
            return
        }

        if(cityEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(addressList.get(2)+"を入力してください。")
            return
        }

        if(tyouEdit.text.toString().equals("")) {
            mSVProgressHUD?.showErrorWithStatus(addressList.get(3)+"を入力してください。")
            return
        }


        seiKanji = lastNameKanjiEdit.text.toString()
        meiKanji = firstNameKanjiEdit.text.toString()
        seiKana = lastNameEdit.text.toString()
        meiKana = firstNameEdit.text.toString()
        phoneNumber = phoneEdit.text.toString()
        postalCode = postalCodeEdit.text.toString()
        prefecture = prefectureTv.text.toString()
        city = cityEdit.text.toString()
        tyou = tyouEdit.text.toString()



        var result = HashMap<String, Any>()
        result.put("seiKanji", seiKanji!!)
        result.put("meiKanji", meiKanji!!)
        result.put("seiKana", seiKana!!)
        result.put("meiKana", meiKana!!)
        result.put("phoneNumber", phoneNumber!!)
        result.put("postalCode", postalCode!!)
        result.put("prefecture", prefecture!!)
        result.put("city", city!!)
        result.put("tyou", tyou!!)

        var building: String? = null
        if (buildEdit.text.toString() != "") {
            building = buildEdit.text.toString()
        }

        building?.let {
            result.put("building", it)
        }

        mSVProgressHUD?.show()
        Api.Address.REF_ADDRESS.child(currentUid).setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

            mSVProgressHUD?.dismiss()

            if (databaseError != null) {

                makeToast(this, databaseError.message)
                return@CompletionListener
            }

            finish()
        })
    }

    fun loadMyAddress() {

        Api.Address.observeAddress(currentUid!!, {address ->

            if (address == null) {
                alertLayout.visibility = View.VISIBLE
            }

            address?.let {

                seiKanji = address.seiKanji
                meiKanji = address.meiKanji
                seiKana = address.seiKana
                meiKana = address.meiKana
                phoneNumber = address.phoneNumber
                postalCode = address.postalCode
                prefecture = address.prefecure
                city = address.city
                tyou = address.tyou

                it.building?.let {
                    buildEdit.setText(it)
                }

                lastNameKanjiEdit.setText(seiKanji)
                firstNameKanjiEdit.setText(meiKanji)
                lastNameEdit.setText(seiKana)
                firstNameEdit.setText(meiKana)
                phoneEdit.setText(phoneNumber)
                postalCodeEdit.setText(postalCode)
                prefectureTv.text = prefecture
                cityEdit.setText(city)
                tyouEdit.setText(tyou)


            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {

            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {

                // 返却されてきたintentから値を取り出す
                val bundle = data?.extras
                val str = bundle?.getString(IntentKey.PREFECTURE.name)

                prefecture = str
                prefectureTv.text = str

            }
        }
    }
}
