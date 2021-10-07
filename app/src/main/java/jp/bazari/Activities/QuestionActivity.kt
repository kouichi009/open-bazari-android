package jp.bazari.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.email_inquiry
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.snippet_searchbar.*


class QuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.mailQuestionTypes, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        backArrow.setOnClickListener {
            finish()
        }

        mailBtn.setOnClickListener {

            val spinnerSelectItem = spinner.selectedItem as String

            val uri = Uri.parse("mailto:"+email_inquiry)

            // ACTION_SENDTOを使用してIntentを生成
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setType("text/plain");
            intent.setData(uri)
            // タイトルを設定
            intent.putExtra(Intent.EXTRA_SUBJECT, spinnerSelectItem)
            // 本文をを設定

            var str: String = ""

            when (spinnerSelectItem) {

                "アプリの不具合" -> {
                    str = "不具合の詳細をご記入ください。\nスクリーンショットがありますと、とても参考になります。"
                }

                "機能の要望" -> {
                    str = "ご連絡いただいた内容は、今後の改善の参考にさせていただきます。\n恐れ入りますが、個別の返信は行っておりません。何卒ご了承ください。"
                }

                "その他" -> {
                    str = "お困りの内容をご記入ください。"
                }

            }

            val appName = "アプリ名: バザリ"
            val osVersion =  "OS: "+android.os.Build.VERSION.SDK_INT
            val kisyu = "機種: "+android.os.Build.MODEL
            var appVersion: String = ""
            try {
                val pInfo = getPackageManager().getPackageInfo(packageName, 0)
                appVersion = "アプリVer: "+pInfo.versionName
                val verCode = pInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            val messageBody = str+"\n\n\n\n\n-------以下の内容はそのままで-----\nアンドロイド"+"\n"+appName+"\n"+appVersion+"\n"+osVersion+"\n"+kisyu
            intent.putExtra(Intent.EXTRA_TEXT, messageBody)
            // メーラー呼び出し
            startActivity(intent)
//            startActivityForResult(intent, 2001)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 2001) {
//            makeToast(this, "メールを送信しました。")
//        }
//    }

}
