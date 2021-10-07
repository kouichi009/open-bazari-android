package jp.bazari.Apis

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import io.repro.android.Repro
import jp.bazari.R

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        getMessageToken()
        setUpRepro()
        wakeUpHeroku()
    }

    fun setUpRepro() {

        if (jp.bazari.BuildConfig.DEBUG) {
            Log.d ("Debug", "これはデバッグビルドですよ！")
        } else {
            Log.d ("Release", "これはリリースビルドですよ！")
            // Setup Repro
            Repro.setup(this, reproToken)
            // Start Recording
            Repro.startRecording();
        }

    }

    fun getMessageToken() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        val token = FirebaseInstanceId.getInstance().getToken()

        token?.let {
            val sharedPrefs = getSharedPreferences(getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString(getString(R.string.FCMToken), it)
            editor.apply()
        }

    }

    // access server to wake up heroku server
    // エラー forbidden (403)が出るが、urlにアクセスできるだけでいいので、これでOK. herokuサーバーは起きる。
    fun wakeUpHeroku() {

        val queue = Volley.newRequestQueue(applicationContext)

        val url = herokuForStripeServerUrl
        val jsonObjRequest = object : StringRequest(Request.Method.GET, url,

            Response.Listener { response ->
                print("Heroku起きた")
            },
            Response.ErrorListener {
                print("Heroku起きた")
            }) {
        }

        queue.add(jsonObjRequest)
    }


    companion object {
        lateinit var appContext: Context
    }
}