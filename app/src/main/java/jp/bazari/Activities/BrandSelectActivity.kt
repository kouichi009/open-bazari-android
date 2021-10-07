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
            "ア行" -> return
            "カ行" -> return
            "サ行" -> return
            "タ行" -> return
            "ナ行" -> return
            "ハ行" -> return
            "マ行" -> return
            "ヤ行" -> return
            "ラ行" -> return
            "その他" -> {
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
        editText.hint = "メーカー名"
        editText.setHintTextColor(Color.GRAY)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("その他のメーカー")
        builder.setMessage("お探しのメーカーが一覧にない場合はメーカー名を入力してください。")
        builder.setCancelable(false)
        builder.setView(editText)

        builder.setPositiveButton("Ok") { dialog, which ->

            val editTex = editText.text.toString()
            if (editTex.equals("")) {
                mSVProgressHUD?.showErrorWithStatus("メーカー名を入力してください。")
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
            "キャンセル"
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
                    "メーカー名なし" -> return@forEach
                    "その他" -> return@forEach
                    "ア行" -> return@forEach
                    "カ行" -> return@forEach
                    "サ行" -> return@forEach
                    "タ行" -> return@forEach
                    "ナ行" -> return@forEach
                    "ハ行" -> return@forEach
                    "マ行" -> return@forEach
                    "ヤ行" -> return@forEach
                    "ラ行" -> return@forEach
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

        //ブランドなし
        //ヘッダー
        arr.add(BrandAll(" ", "", ""))
        arr.add(BrandAll("メーカー名なし", "", ""))
        arr.add(BrandAll("その他", "", ""))

        //ヘッダー / ア行
        arr.add(BrandAll("ア行", "", ""))
        arr.add(BrandAll("OLYMPUS", "オリンパス", "おりんぱす"))

        //カ行
        arr.add(BrandAll("カ行", "", ""))
        arr.add(BrandAll("CASIO", "カシオ", "かしお"))
        arr.add(BrandAll("Carl Zeiss", "カールツァイス", "かーるつぁいす"))
        arr.add(BrandAll("Canon", "キャノン", "きゃのん"))
        arr.add(BrandAll("KenkoTokina", "ケンコー トキナー", "けんこー ときなー"))
        arr.add(BrandAll("Gateway", "ゲートウェイ", "げーとうぇい"))
        arr.add(BrandAll("COSINA", "コシナ", "こしな"))
        arr.add(BrandAll("KONICA MINOLTA", "コニカ ミノルタ", "こにか みのるた"))
        arr.add(BrandAll("CONTAX", "コンタックス", "こんたっくす"))


        //サ行
        arr.add(BrandAll("サ行", "", ""))
        arr.add(BrandAll("SAMSUNG", "サムスン", "さむすん"))
        arr.add(BrandAll("SAMYANG", "サムヤン", "さむやん"))
        arr.add(BrandAll("SANYO", "サンヨー", "さんよー"))
        arr.add(BrandAll("SIGMA", "シグマ", "しぐま"))
        arr.add(BrandAll("SHARP", "シャープ", "しゃーぷ"))
        arr.add(BrandAll("Schneider", "シュナイダー", "しゅないだー"))
        arr.add(BrandAll("SONY", "ソニー", "そにー"))

        //タ行
        arr.add(BrandAll("タ行", "", ""))
        arr.add(BrandAll("TAMRON", "タムロン", "たむろん"))
        arr.add(BrandAll("TOKINA", "トキナー", "ときなー"))

        //ナ行
        arr.add(BrandAll("ナ行", "", ""))
        arr.add(BrandAll("Nikon", "ニコン", "にこん"))

        //ハ行
        arr.add(BrandAll("ハ行", "", ""))
        arr.add(BrandAll("Hasselblad", "ハッセルブラッド", "はっせるぶらっど"))
        arr.add(BrandAll("Panasonic", "パナソニック", "ぱなそにっく"))
        arr.add(BrandAll("FUJIFILM", "富士フィルム", "ふじふぃるむ"))
        arr.add(BrandAll("Planar", "プラナー", "ぷらなー"))
        arr.add(BrandAll("PENTAX", "ペンタックス", "ぺんたっくす"))

        //マ行
        arr.add(BrandAll("マ行", "", ""))
        arr.add(BrandAll("Mamiya", "マミヤ", "まみや"))

        //ラ行
        arr.add(BrandAll("ラ行", "", ""))
        arr.add(BrandAll("Leica", "ライカ", "らいか"))
        arr.add(BrandAll("RICOH", "リコー", "りこー"))
        arr.add(BrandAll("Rollei", "ローライ", "ろーらい"))
        return arr

    }
}