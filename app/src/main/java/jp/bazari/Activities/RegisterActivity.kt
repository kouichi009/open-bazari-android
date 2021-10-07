package jp.bazari.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import io.repro.android.Repro
import jp.bazari.Apis.Api
import jp.bazari.Apis.AuthService
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Apis.makeToast
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    var mSVProgressHUD: SVProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mSVProgressHUD = SVProgressHUD(this)

        reproAnalyticsRegisterVC_ViewDidLoad()

        makeToast(this, "会員登録する必要があります。")

        facebookLayout.setOnClickListener {
            //FacebookTapped
            AuthService.FBLogin(mSVProgressHUD!!,this, {
                //onSuccess
                reproAnalyticsFacebookBtnTapped()
                //ログインであれば、
                if (it) {
                    Api.User.observeUser(getCurrentUser()!!.uid, {usermodel ->

                        usermodel?.let {
                            //削除済みユーザーであれば、ログアウトする。
                            if (it.isCancel != null) {
                                AuthService.logout {
                                    mSVProgressHUD?.dismiss()
                                    makeToast(this,"このアカウントは削除済みでログインできません。")
                                }
                            } else {
                                mSVProgressHUD?.dismiss()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    })
                }
                //新規登録であれば
                else {
                    mSVProgressHUD?.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

            }, {
                //onError
                mSVProgressHUD?.dismiss()
                makeToast(this, "ログインエラー")

            })
        }

        emailRegisterLayout.setOnClickListener {
            reproAnalyticsEmailRegisterBtnTapped()
            val intent = Intent(this, EmailRegisterActivity::class.java)
            startActivity(intent)
        }

        emailLoginLayout.setOnClickListener {
            reproAnalyticsEmailLoginBtnTapped()
            val intent = Intent(this, EmailLoginActivity::class.java)
            startActivity(intent)
        }

        emailLoginLayout2.setOnClickListener {
            reproAnalyticsEmailLoginBtnTapped()
            val intent = Intent(this, EmailLoginActivity::class.java)
            startActivity(intent)

        }

        backLayout.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.kiyakusharedPref), Context.MODE_PRIVATE)

        val isKiyakuAgree = sharedPreferences.getBoolean(getString(R.string.isKiyakuAgreeKey), false)
        if (!isKiyakuAgree) {
            val intent = Intent(this@RegisterActivity, KiyakuAgreeActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AuthService.callbackManager?.onActivityResult(requestCode, resultCode, data)
    }


    fun reproAnalyticsRegisterVC_ViewDidLoad() {

        // Custom event
        Repro.track("新規登録・ログイン画面")
    }

    fun reproAnalyticsFacebookBtnTapped() {

        // Custom event
        Repro.track("Facebook登録・ログイン")
    }

    fun reproAnalyticsEmailRegisterBtnTapped() {

        // Custom event
        Repro.track("メルアド新規登録画面へ")
    }

    fun reproAnalyticsEmailLoginBtnTapped() {

        // Custom event
        Repro.track("メルアドログイン画面へ")
    }
}
