package jp.bazari.Apis

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.bigkoo.svprogresshud.SVProgressHUD
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import jp.bazari.Activities.RegisterActivity
import jp.bazari.R
import org.json.JSONException
import java.util.*



object AuthService {

    var callbackManager: CallbackManager? = null

    fun signUp(profileImageUrl: String, username: String, email: String, password: String, loginType: String,
               onSuccess: (Boolean) -> Unit, onError: (String?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
            if (!task.isSuccessful) {

                val exception = task.exception
                val message = exception?.message
                val printStack = exception?.printStackTrace()
                onError(message)
                return@addOnCompleteListener
            }

            val uid = task.result.user.uid
            setUserInformation(true, profileImageUrl, username, email, uid, loginType, onSuccess, onError)
        }
    }

    // onSuccess: (Boolean) は、Facebookログインで新規登録か、すでに登録済みでログインするだけか判定するために、Booleanを使う。Emailでの新規登録では、Booleanは関係ない。
    fun setUserInformation(
        isEmailAuth: Boolean?, profileImageUrl: String, username: String, email: String?, uid: String, loginType: String
        , onSuccess: (Boolean) -> Unit, onError: (String?) -> Unit) {
        val sharedPrefs = MyApplication.appContext.getSharedPreferences(MyApplication.appContext.getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE)
        val fcmToken = sharedPrefs.getString(MyApplication.appContext.getString(R.string.FCMToken), "NoToken")
        val timestamp = getTimestamp()

        var result = HashMap<String, Any>()
        result.put("username", username)
        result.put("username_lowercase", username.toLowerCase())
        result.put("profileImageUrl", profileImageUrl)
        result.put("loginType", loginType)
        result.put("timestamp", timestamp)
        result.put("fcmToken", fcmToken)
        email?.let {
            result.put("email", it)
        }
        isEmailAuth?.let {
            result.put("isEmailAuth", it)
        }
        val values = HashMap<String, Int>()
        values.put("sun", 0)
        values.put("cloud", 0)
        values.put("rain", 0)
        result.put("value", values)

        Api.User.REF_USERS.child(uid).setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->
            if (databaseError != null) {

                onError(databaseError.message)
                return@CompletionListener
            }
        })

        var result2 = HashMap<String, Any>()
        result2.put("checked", false)
        result2.put("from", "admin")
        result2.put("objectId", "バザリへのご登録ありがとうございます。")
        result2.put("segmentType", "you")
        result2.put("timestamp", timestamp)
        result2.put("to", uid)
        result2.put("type", "admin")

        val autoKey = Api.Notification.REF_NOTIFICATION.child(uid).push().key
        Api.Notification.REF_MYNOTIFICATION.child(uid).child(autoKey).child("timestamp").setValue(timestamp)
        Api.Notification.REF_NOTIFICATION.child(autoKey).setValue(result2)

        //falseなのは、Facebookでの新規登録のときに使う。Emailでの新規登録では関係ない。
        onSuccess(false)
    }

    fun signIn(email: String, password: String, onSuccess: (String) -> Unit, onError: (String?) -> Unit) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
            if (!task.isSuccessful) {

                onError(MyApplication.appContext.getString(R.string.firebase_auth_failed))
                return@addOnCompleteListener
            }

            val uid = task.result.user.uid
            onSuccess(task.result.user.uid)
        }
    }

    fun FBLogin(mSVProgressHUD: SVProgressHUD, registerActivity: RegisterActivity, onSuccess: (Boolean) -> Unit, onError: (String?) -> Unit) {

        LoginManager.getInstance().logInWithReadPermissions(registerActivity,
            Arrays.asList("public_profile","email"/*,"user_friends"*/))

        callbackManager = CallbackManager.Factory.create()
        //FacebookLogin
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                mSVProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)

                // App code
                val request = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response ->

                    try {
                        // Application code
                        val email = `object`.getString("email")
                        val name = `object`.getString("name")
//                        val picture0 = response.jsonObject.getJSONObject("picture")
//                        val picture = `object`.getJSONObject("picture");
                        handleFacebookAccessToken(loginResult.accessToken, email, name, onSuccess, onError)

                    } catch (e: JSONException) {
                        onError("error")
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                onError("error")
            }

            override fun onError(error: FacebookException) {
                onError("error")
            }
        })
    }

    fun handleFacebookAccessToken(accessToken: AccessToken, email: String?, name: String?,
                                  onSuccess: (Boolean) -> Unit, onError: (String?) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(credential).addOnFailureListener {
            //error
            Log.d("faceError", it.message)
            onError(it.message)
        }.addOnSuccessListener { authResult ->
            // success
            val uid = authResult.user.uid
            isRegisteredFB(uid, email, name, onSuccess, onError)
        }
    }

    fun isRegisteredFB(uid: String, email: String?, name: String?, onSuccess: (Boolean) -> Unit, onError: (String?) -> Unit) {

        var username = name
        var emailStr = email
        var keyCheck = false
        var count = 0


        Api.User.observeUsers({ usermodel, usersCount ->

            val a = usermodel
            val b = usersCount

            if (usersCount == 0) {
                setUserInformation(null, anonymousImageURL, username!!, email, uid, LoginType.Facebook.toString(),
                    onSuccess, onError)
                return@observeUsers
            }

            count++
            usermodel?.let {

                if (uid == it.id) {
                    keyCheck = true
                }
            }

            if (count == usersCount) {

                if (keyCheck) {
                    //"ロード完了"
                    onSuccess(true)
                } else {
                    //"新規登録")

                    if (username == null) {
                        username = ""
                    }

                    if (emailStr == null) {
                        emailStr = ""
                    }

                    setUserInformation(null, anonymousImageURL, username!!, email, uid, LoginType.Facebook.toString(),
                        onSuccess, onError)
                }
            }
        })
    }

    fun logout(onSuccess: () -> Unit) {

        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut();
        onSuccess()
    }
}
