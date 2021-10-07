package jp.bazari.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.ligl.android.widget.iosdialog.IOSDialog
import jp.bazari.Apis.Api
import jp.bazari.Apis.CardType
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.getCurrentUser
import jp.bazari.R
import jp.bazari.models.CardModel
import kotlinx.android.synthetic.main.activity_display_card.*
import kotlinx.android.synthetic.main.snippet_top_card_display.*
import java.util.*
import kotlin.concurrent.schedule


class DisplayCardActivity : AppCompatActivity() {

    lateinit var currentUid: String
    var myCards = ArrayList<CardModel>()
    var selectedCard : CardModel? = null

    val registerCreditCardMoji = "クレジットカードを登録する"

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_card)

        currentUid = getCurrentUser()!!.uid

        backArrow.setOnClickListener {
            finish()
        }

        textView.text = registerCreditCardMoji

        mSVProgressHUD = SVProgressHUD(this)

        val bundle = intent.extras
        val activityNameFrom = bundle.getString(IntentKey.WHICHACTIVITYTODISTINGUISH.toString())

        registerLayout.setOnClickListener {

            if(textView.text == registerCreditCardMoji) {
                val intent = Intent(this, CreditCardRegisterActivity::class.java).apply {
                    putExtra(IntentKey.CARDS.name, myCards)
                    putExtra(IntentKey.WHICHACTIVITYTODISTINGUISH.name, activityNameFrom)
                }
                startActivity(intent)
            }

        }

        option_editBtn.setOnClickListener {

                push_option_edit()
        }

        cardIv.setOnClickListener {

            if(textView.text == registerCreditCardMoji) {
                val intent = Intent(this, CreditCardRegisterActivity::class.java).apply {
                    putExtra(IntentKey.CARDS.name, myCards)
                    putExtra(IntentKey.WHICHACTIVITYTODISTINGUISH.name, "SettingActivity")
                }
                startActivity(intent)
                return@setOnClickListener
            }

            if (option_editBtn.text == "完了"){
                deleteTap()
            }
        }

        isExistActiveCard()
    }

    fun push_option_edit() {
        selectedCard?.let {
            if(option_editBtn.text == "完了") {

                if (it.cardType == CardType.visa.toString()) {
                    cardIv.setImageResource(R.drawable.visa)
                } else if(it.cardType == CardType.mastercard.toString()) {
                    cardIv.setImageResource(R.drawable.master)
                }

                option_editBtn.text = "編集"
            } else {
                cardIv.setImageResource(R.drawable.redclose_circle)
                option_editBtn.text = "完了"
            }
        }
    }

    fun deleteTap() {
        IOSDialog.Builder(this)
            .setTitle("クレジットカード情報の消去")
            .setMessage("カード情報を消去してよろしいですか？")
            // .setMessage("1")
            .setPositiveButton(
                "はい"
            ) { dialog, which ->


                mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)


                Timer().schedule(3000, {
                    val result = HashMap<String, Any>()
                    result.put("deletedFlg", true)
                    Api.Card.REF_CARDS.child(currentUid).child(selectedCard!!.id).updateChildren(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                        if (databaseError != null) {

                            runOnUiThread {

                                mSVProgressHUD?.dismiss()
                                Timer().schedule(1000, {

                                    runOnUiThread {
                                    mSVProgressHUD?.showErrorWithStatus("エラー発生")
                                    }
                                })
                            }
                            return@CompletionListener
                        }

                        runOnUiThread {
                            mSVProgressHUD?.dismiss()
                            finish()
                        }
                    })
                })

            }
            .setNegativeButton(
                "いいえ", null
            ).show()
    }

    fun isExistActiveCard() {

        var count = 0
        Api.Card.observeCard(currentUid, {cardmodel, cardCount ->

            if (cardCount == 0) {
                setCreditCardAddForm()
                return@observeCard
            }

            count += 1

            cardmodel?.let {
                myCards.add(it)

                //deletedFlgがnilつまり、trueでない場合なので、削除されてない使えるクレジットカードをselectedCardDataに入れる。
                if (it.deletedFlg == null) {
                    selectedCard = it
                }
            }

            if (count == cardCount) {
                // 使えるカードが一枚もない場合、
                if (selectedCard == null) {
                    setCreditCardAddForm()
                } else {
                    setCreditCard()
                }
            }
        })
    }

    fun setCreditCardAddForm() {
        cardIv.setImageResource(R.drawable.addcard)
        textView.setTextColor(Color.parseColor("#6FC1A5"))

    }

    fun setCreditCard() {
        if(selectedCard!!.cardType == CardType.visa.toString()) {
            cardIv.setImageResource(R.drawable.visa)
        } else if (selectedCard!!.cardType == CardType.mastercard.toString()) {
            cardIv.setImageResource(R.drawable.master)
        }

        val last4moji_monthYear = selectedCard!!.id!!
        val last4moji = last4moji_monthYear.substring(0,4)
        val monthYear = last4moji_monthYear.substring(4,8)
        val month = monthYear.substring(0,2)
        val year = monthYear.substring(2,4)

        val cardNumber = "************ " + last4moji + "  "
        val monthYearWithSlash = month+"/"+year

        val cardInfoMoji = cardNumber+monthYearWithSlash
        textView.text = cardInfoMoji
        textView.setTextColor(Color.parseColor("#000000"))
    }
}
