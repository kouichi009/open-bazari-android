package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import com.ligl.android.widget.iosdialog.IOSDialog
import io.repro.android.Repro
import jp.bazari.Apis.Api
import jp.bazari.Apis.AuthService
import jp.bazari.Apis.makeToast
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_email_login.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*

class EmailLoginActivity : AppCompatActivity() {

    lateinit var email: String
    lateinit var password: String

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        mSVProgressHUD = SVProgressHUD(this)

        backArrow.setOnClickListener {
            finish()
        }

        loginBtn.setOnClickListener {

            email = emailEdit.text.toString()
            password = passwordEdit.text.toString()

            if (email.equals("")) {
                showErrorAlert("メールアドレスを入力してください。")
                return@setOnClickListener

            } else if (password.equals("")) {
                showErrorAlert("パスワードを入力してください。")
                return@setOnClickListener

            }

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)

            AuthService.signIn(email, password, { uid ->
                //onSuccess

                Api.User.observeUser(uid, {usermodel ->

                    usermodel?.let {
                        //削除済みユーザーであれば、ログアウトする。
                        if (it.isCancel != null) {
                            AuthService.logout {
                                mSVProgressHUD?.dismiss()
                                showErrorAlert("メールアドレス・パスワードが一致しません。")
                            }
                        } else {
                            mSVProgressHUD?.dismiss()
                            reproAnalyticsEmailLogin()
                            makeToast(this, "ログインしました")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })


            }, { error ->
                mSVProgressHUD?.dismiss()
                showErrorAlert("メールアドレス・パスワードが一致しません。")
            })
        }
    }

    private fun showErrorAlert(errorMessage: String) {
        IOSDialog.Builder(this)
            .setTitle("エラー")
            .setMessage(errorMessage)
            .setPositiveButton("OK", null).show()
    }

    fun reproAnalyticsEmailLogin() {

        // Custom event
        Repro.track("メルアドログイン")
    }

}
