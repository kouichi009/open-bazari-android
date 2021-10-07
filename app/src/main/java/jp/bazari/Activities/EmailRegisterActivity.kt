package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import com.ligl.android.widget.iosdialog.IOSDialog
import io.repro.android.Repro
import jp.bazari.Apis.AuthService
import jp.bazari.Apis.LoginType
import jp.bazari.Apis.anonymousImageURL
import jp.bazari.Apis.makeToast
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_email_register.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class EmailRegisterActivity : AppCompatActivity() {

    lateinit var email: String
    lateinit var password: String
    lateinit var username: String

    var mSVProgressHUD: SVProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_register)


        mSVProgressHUD = SVProgressHUD(this)

        backArrow.setOnClickListener {
            finish()
        }

        registerBtn.setOnClickListener {

            email = emailEdit.text.toString()
            password = passwordEdit.text.toString()
            username = usernameEdit.text.toString()

            if (email.equals("")) {
                showErrorAlert("メールアドレスを入力してください。")
                return@setOnClickListener

            } else if (password.equals("")) {
                showErrorAlert("パスワードを入力してください。")
                return@setOnClickListener

            } else if (username.equals("")) {
                showErrorAlert("ニックネームを入力してください。")
                return@setOnClickListener
            }

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)

            AuthService.signUp(anonymousImageURL, username, email, password, LoginType.Email.toString(), {
                //OnSuccess
                mSVProgressHUD?.dismiss()
                makeToast(this, "登録しました")
                reproAnalyticsEmailRegister()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            },{error ->
                //OnError
                mSVProgressHUD?.dismiss()

                error?.let {

                    lateinit var errorMessage: String

                    if (it.contains("Password should be at least 6 characters")) {

                        errorMessage = "パスワードは6文字以上にしてください。"
                    } else if (it.contains("email address is already in use")) {

                        errorMessage = "他のアカウントで既に登録済みのメールアドレスです。"
                    } else {

                        errorMessage = "登録できません。入力に不備があるようです。"
                    }
                    showErrorAlert(errorMessage)
                }
            })
        }
    }

    private fun showErrorAlert(errorMessage: String) {
        IOSDialog.Builder(this)
            .setTitle("エラー")
            .setMessage(errorMessage)
            .setPositiveButton("OK", null).show()
    }

    fun reproAnalyticsEmailRegister() {
        // Custom event
        Repro.track("メルアド新規登録")
    }
}
