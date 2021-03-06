package jp.bazari.Activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.bigkoo.svprogresshud.SVProgressHUD
import jp.bazari.Adapters.BrandSelectAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.getCurrentUser
import jp.bazari.R
import jp.bazari.models.BrandAll
import kotlinx.android.synthetic.main.activity_brand_select.*
import kotlinx.android.synthetic.main.snippet_searchbar.*


class BrandSelectActivity : AppCompatActivity() {

    var brandSelectAdapter: BrandSelectAdapter? = null

    var selectedBrandArray: ArrayList<BrandAll> = ArrayList()

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_select)

        selectedBrandArray = getDefultList()

        brandSelectAdapter = BrandSelectAdapter(this, selectedBrandArray)
        listView.adapter = brandSelectAdapter

        mSVProgressHUD = SVProgressHUD(this)


        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(text: Editable) {
                afterTex(text)
            }
        })

        backArrow.setOnClickListener {
            finish()
        }

        closeBtn.setOnClickListener {
            searchEdit.setText("")
            closeKeyboard()
        }

        listView.setOnItemClickListener { parent, view, position, id ->

            clickListView(position)

        }
    }

    private fun clickListView(position: Int) {

        when(selectedBrandArray[position].enName) {

            " " -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "??????" -> return
            "?????????" -> {
                showAlertSonota()
                return
            }
        }

        val intent = Intent()
        intent.putExtra(IntentKey.BRAND.name, selectedBrandArray[position].enName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showAlertSonota() {
        val editText = EditText(this)
        editText.setTextColor(Color.BLACK)
        editText.hint = "???????????????"
        editText.setHintTextColor(Color.GRAY)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("????????????????????????")
        builder.setMessage("????????????????????????????????????????????????????????????????????????????????????????????????")
        builder.setCancelable(false)
        builder.setView(editText)

        builder.setPositiveButton("Ok") { dialog, which ->

            val editTex = editText.text.toString()
            if (editTex.equals("")) {
                mSVProgressHUD?.showErrorWithStatus("?????????????????????????????????????????????")
                return@setPositiveButton
            }

            val key = Api.Brand.REF_BRAND_CANDIDATES.child(getCurrentUser()!!.uid).push().key
            Api.Brand.REF_BRAND_CANDIDATES.child(getCurrentUser()!!.uid).child(key).child("brand").setValue(editTex)

            val a = editTex

            val intent = Intent()
            intent.putExtra(IntentKey.BRAND.name, editTex)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        builder.setNegativeButton(
            "???????????????"
        ) { dialog, which -> dialog.dismiss() }

        builder.show()
    }


    private fun afterTex(text: CharSequence) {

        selectedBrandArray.clear()

        if (text.toString().length > 0) {

            getDefultList().forEach { brandAll ->

                val a = brandAll.enName.toLowerCase()
                val first = brandAll.enName.toLowerCase().substring(0, 1)
                val count = text.count()

                when(brandAll.enName) {

                    " " -> return@forEach
                    "?????????????????????" -> return@forEach
                    "?????????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                    "??????" -> return@forEach
                }

                if (text.count() <= 1 && brandAll.enName.toLowerCase().substring(0, 1) == text.toString().toLowerCase() ||
                    text.count() <= 1 && brandAll.jpName.toLowerCase().substring(0, 1) == text.toString().toLowerCase() ||
                    text.count() <= 1 && brandAll.jpHira.toLowerCase().substring(0, 1) == text.toString().toLowerCase()) {

                    selectedBrandArray.add(brandAll)

                }
                if (text.count() > 1 && brandAll.enName.toLowerCase().contains(text.toString().toLowerCase()) ||
                    text.count() > 1 && brandAll.jpName.toLowerCase().contains(text.toString().toLowerCase()) ||
                    text.count() > 1 && brandAll.jpHira.toLowerCase().contains(text.toString().toLowerCase())) {

                    selectedBrandArray.add(brandAll)
                }

            }

            brandSelectAdapter = BrandSelectAdapter(this@BrandSelectActivity, selectedBrandArray)
            listView.adapter = brandSelectAdapter

        } else {

            selectedBrandArray = getDefultList()
            brandSelectAdapter = BrandSelectAdapter(this@BrandSelectActivity, selectedBrandArray)
            listView.adapter = brandSelectAdapter


        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getDefultList(): ArrayList<BrandAll> {

        var arr: ArrayList<BrandAll> = ArrayList()

        //??????????????????
        //????????????
        arr.add(BrandAll(" ", "", ""))
        arr.add(BrandAll("?????????????????????", "", ""))
        arr.add(BrandAll("?????????", "", ""))

        //???????????? / ??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("OLYMPUS", "???????????????", "???????????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("CASIO", "?????????", "?????????"))
        arr.add(BrandAll("Carl Zeiss", "?????????????????????", "?????????????????????"))
        arr.add(BrandAll("Canon", "????????????", "????????????"))
        arr.add(BrandAll("KenkoTokina", "???????????? ????????????", "???????????? ????????????"))
        arr.add(BrandAll("Gateway", "??????????????????", "??????????????????"))
        arr.add(BrandAll("COSINA", "?????????", "?????????"))
        arr.add(BrandAll("KONICA MINOLTA", "????????? ????????????", "????????? ????????????"))
        arr.add(BrandAll("CONTAX", "??????????????????", "??????????????????"))


        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("SAMSUNG", "????????????", "????????????"))
        arr.add(BrandAll("SAMYANG", "????????????", "????????????"))
        arr.add(BrandAll("SANYO", "????????????", "????????????"))
        arr.add(BrandAll("SIGMA", "?????????", "?????????"))
        arr.add(BrandAll("SHARP", "????????????", "????????????"))
        arr.add(BrandAll("Schneider", "??????????????????", "??????????????????"))
        arr.add(BrandAll("SONY", "?????????", "?????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("TAMRON", "????????????", "????????????"))
        arr.add(BrandAll("TOKINA", "????????????", "????????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("Nikon", "?????????", "?????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("Hasselblad", "????????????????????????", "????????????????????????"))
        arr.add(BrandAll("Panasonic", "??????????????????", "??????????????????"))
        arr.add(BrandAll("FUJIFILM", "??????????????????", "??????????????????"))
        arr.add(BrandAll("Planar", "????????????", "????????????"))
        arr.add(BrandAll("PENTAX", "??????????????????", "??????????????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("Mamiya", "?????????", "?????????"))

        //??????
        arr.add(BrandAll("??????", "", ""))
        arr.add(BrandAll("Leica", "?????????", "?????????"))
        arr.add(BrandAll("RICOH", "?????????", "?????????"))
        arr.add(BrandAll("Rollei", "????????????", "????????????"))
        return arr

    }
}