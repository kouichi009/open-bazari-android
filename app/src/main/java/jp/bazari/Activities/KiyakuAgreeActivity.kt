package jp.bazari.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_kiyaku_agree.*

class KiyakuAgreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kiyaku_agree)

        imageView.setImageResource(R.drawable.bazari_logo_256)

        val text = "利用規約をご確認の上、登録に進んでください"

        val ss = SpannableString(text)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {

                val intent = Intent(this@KiyakuAgreeActivity, KiyakuExplainActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = true
            }
        }


        ss.setSpan(clickableSpan1, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        kiyakuTv.setText(ss)
        kiyakuTv.setMovementMethod(LinkMovementMethod.getInstance())

        agreeBtn.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.kiyakusharedPref), Context.MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            editor.putBoolean(getString(R.string.isKiyakuAgreeKey), true)
            editor.apply()
            finish()
        }
    }
}
