package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.StripeUtils.StripeUtil
import jp.bazari.models.CardModel
import kotlinx.android.synthetic.main.activity_credit_card_register.*
import kotlinx.android.synthetic.main.snippet_top_card_display.*
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule


class CreditCardRegisterActivity : AppCompatActivity() {

    lateinit var mStripe: Stripe

    var mCard: Card? = null

    var currentUid: String? = null

    var mCards = ArrayList<CardModel>()

    var mCustomerId: String? = null

    var mUidEmail: String? = null

    var mSVProgressHUD: SVProgressHUD? = null

    var activityNameFrom = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card_register)

        currentUid = getCurrentUser()!!.uid
        mUidEmail = currentUid!! + "@gmail.com"

        mSVProgressHUD = SVProgressHUD(this)

        mStripe = Stripe(this, StripeApiKey)

        val bundle = intent.extras
        mCards = bundle.getSerializable(IntentKey.CARDS.name) as ArrayList<CardModel>
        activityNameFrom = bundle.getString(IntentKey.WHICHACTIVITYTODISTINGUISH.toString())

        backArrow.setOnClickListener {
            finish()
        }

        registerBtn.setOnClickListener {
            pushRegisterBtn()
        }

        questionIv.setOnClickListener {

            FancyGifDialog.Builder(this)
                .setMessage("カードの裏面に記載されている３ケタの番号を入力してください。")
                .setNegativeBtnText("")
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground("#FFFFFF")
                .setGifResource(R.drawable.cvcimage)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked {
                }
                .build()
        }

        val cardNumberEditText = findViewById(R.id.cardNumEdit) as EditText
        cardNumberEditText.addTextChangedListener(object : TextWatcher {

            private val space = "-" // you can change this to whatever you want
            private val pattern =
                Pattern.compile("^(\\d{4}$space{1}){0,3}\\d{1,4}$") // check whether we need to modify or not

            override fun onTextChanged(s: CharSequence, st: Int, be: Int, count: Int) {
                val currentText = cardNumberEditText.getText().toString()
                if (currentText.isEmpty() || pattern.matcher(currentText).matches())
                    return  // no need to modify
                val numbersOnly = currentText.trim({ it <= ' ' }).replace("[^\\d.]".toRegex(), "")
                var formatted = ""
                var i = 0
                while (i < numbersOnly.length) {
                    if (i + 4 < numbersOnly.length)
                        formatted += numbersOnly.substring(i, i + 4) + space
                    else
                        formatted += numbersOnly.substring(i)
                    i += 4
                }
                cardNumberEditText.setText(formatted)
                cardNumberEditText.setSelection(cardNumberEditText.getText().toString().length)
            }// remove everything but numbers

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(e: Editable) {}
        })


        val expireEditText = findViewById(R.id.expireEdit) as EditText
        expireEditText.addTextChangedListener(object : TextWatcher {

            private val TOTAL_SYMBOLS = 5 // size of pattern 0000-0000-0000-0000
            private val TOTAL_DIGITS = 4 // max numbers of digits in pattern: 0000 x 4
            private val DIVIDER_MODULO = 3 // means divider position is every 5th symbol beginning with 1
            private val DIVIDER_POSITION =
                DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
            private val DIVIDER = '/'

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // noop
            }

            override fun afterTextChanged(s: Editable) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(
                        0,
                        s.length,
                        buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER)
                    )
                }
            }

            private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
                var isCorrect = s.length <= totalSymbols // check size of entered string
                for (i in 0 until s.length) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect = isCorrect and (divider == s[i])
                    } else {
                        isCorrect = isCorrect and Character.isDigit(s[i])
                    }
                }
                return isCorrect
            }

            private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    if (digits[i].toInt() != 0) {
                        formatted.append(digits[i])
                        if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                            formatted.append(divider)
                        }
                    }
                }

                return formatted.toString()
            }

            private fun getDigitArray(s: Editable, size: Int): CharArray {
                val digits = CharArray(size)
                var index = 0
                var i = 0
                while (i < s.length && index < size) {
                    val current = s[i]
                    if (Character.isDigit(current)) {
                        digits[index] = current
                        index++
                    }
                    i++
                }
                return digits
            }
        })
    }


    fun pushRegisterBtn() {

        if (cardNumEdit.text.toString().count() < 19) {
            IOSDialog.Builder(this)
                .setTitle("無効なカードです。")
                .setMessage("カード番号が誤っています。")
                .setPositiveButton("OK", null).show()
            return
        } else if (expireEdit.text.toString().count() < 5) {
            IOSDialog.Builder(this)
                .setTitle("期限が無効です。")
                .setMessage("期限の入力が誤っています。")
                .setPositiveButton("OK", null).show()
                .show()
            return

        } else if (cvvEdit.text.toString().count() < 3) {
            IOSDialog.Builder(this)
                .setTitle("cvvが無効です。")
                .setMessage("cvcの入力が誤っています。")
                .setPositiveButton("OK", null).show()
            return
        }

        if (cardNumEdit.text.toString().substring(0,1) == "4" ||
            cardNumEdit.text.toString().substring(0,1) == "5") {
            IOSDialog.Builder(this)
                .setTitle("カード情報を登録していいですか？")
                .setPositiveButton(
                    "はい"
                ) { dialog, which ->
                    registerCard()
                }
                .setNegativeButton(
                    "いいえ", null
                ).show()
        }
        else {
            IOSDialog.Builder(this)
                .setMessage("使用できるカードは、VisaとMasterCardの２種類だけです。")
                .setPositiveButton("OK", null).show()
            return
        }


    }

    fun registerCard() {

        var cardNumTex = cardNumEdit.text.toString()
        cardNumTex = cardNumTex.replace("-", "")
        val last4moji = cardNumTex.substring(12, 16)
        var expire = expireEdit.text.toString()
        val separated = expire.split("/")
        val month = separated[0].toInt()
        val year = separated[1].toInt()

        val last4moji_monthYear = last4moji + separated[0] + separated[1]

        val alreadyExistCardNumTex = isDeletedCardAlreadyExistInFirebaseDatabase(last4moji_monthYear)
        if (alreadyExistCardNumTex != "" && mCards.count() > 0) {

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)
            Timer().schedule(3000, {

                Api.Card.REF_CARDS.child(currentUid).child(alreadyExistCardNumTex).child("deletedFlg").removeValue()
                runOnUiThread {
                    goBackTwoActivity()
                }

            })
            return
        }

        mCard = Card(
            cardNumTex,
            month,
            year,
            cvvEdit.text.toString()
        )

        mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)
        StripeUtil.getUserList(this, mUidEmail!!, { customersJsonArray ->

            customersJsonArray?.let {
                //もし、すでにCustomer情報が登録されていたら、
                if (it.length() > 0) {
                    val customerJsonObj = it.get(0) as JSONObject
                    mCustomerId = customerJsonObj.get("id") as String
                    StripeUtil.createCard(this, mStripe, mCustomerId!!, mCard!!, { success ->

                        if (success == false) {

                            runOnUiThread {
                                IOSDialog.Builder(this)
                                    .setTitle("無効なカードです。")
                                    .setMessage("(エラー) カード情報の入力が誤っている可能性があります。")
                                    .setPositiveButton("OK", null).show()
                                mSVProgressHUD?.dismiss()
                            }

                            Log.d("error", "無効なカードです。")

                            return@createCard
                        }

                        setFirebaseDatabase(last4moji_monthYear)

                    })

                    return@getUserList
                }

                //まだCustomer情報を登録していなかったら
                StripeUtil.createUser(this, mStripe, mCard!!, mUidEmail!!, { success ->

                    if (success == false) {
                        runOnUiThread {
                            IOSDialog.Builder(this)
                                .setTitle("無効なカードです。")
                                .setMessage("(エラー) カード情報の入力が誤っている可能性があります。")
                                .setPositiveButton("OK", null).show()
                            mSVProgressHUD?.dismiss()
                        }

                        Log.d("error", "無効なカードです。")
                        return@createUser
                    }

                    setFirebaseDatabase(last4moji_monthYear)
                })
            }
        })
    }

    fun isDeletedCardAlreadyExistInFirebaseDatabase(last4moji_monthYear: String): String {

        var alreadyExistCardNumTex = ""
        mCards.forEach { cardmodel ->
            if (cardmodel.id == last4moji_monthYear) {
                alreadyExistCardNumTex = cardmodel.id!!
            }
        }

        return alreadyExistCardNumTex
    }


    fun setFirebaseDatabase(cardLast4_DateMoji: String) {

        if (cardNumEdit.text.toString().substring(0, 1) == "4") {
            var result = HashMap<String, Any>()
            result.put("cardType", CardType.visa.toString())
            Api.Card.REF_CARDS.child(currentUid).child(cardLast4_DateMoji)
                .setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                    runOnUiThread {
                        goBackTwoActivity()

                    }
                })
        } else if (cardNumEdit.text.toString().substring(0, 1) == "5") {
            var result = HashMap<String, Any>()
            result.put("cardType", CardType.mastercard.toString())
            Api.Card.REF_CARDS.child(currentUid).child(cardLast4_DateMoji)
                .setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                    runOnUiThread {
                        goBackTwoActivity()
                    }
                })
        }
    }

    fun goBackTwoActivity () {

        mSVProgressHUD?.dismiss()

        if (activityNameFrom == "SettingActivity") {
            val intent = Intent(this@CreditCardRegisterActivity, SettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(intent)

        } else if (activityNameFrom == "PurchaseActivity") {
            val intent = Intent(this@CreditCardRegisterActivity, PurchaseActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(intent)
        }
    }
}