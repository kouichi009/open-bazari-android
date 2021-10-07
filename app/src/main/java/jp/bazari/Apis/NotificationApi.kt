package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Notification
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NotificationApi {

    lateinit var REF_NOTIFICATION: DatabaseReference
    lateinit var REF_MYNOTIFICATION: DatabaseReference
    lateinit var REF_EXIST_NOTIFICATION: DatabaseReference

    constructor() {
        this.REF_NOTIFICATION = FirebaseDatabase.getInstance().getReference(DBKey.notification.toString())
        this.REF_MYNOTIFICATION = FirebaseDatabase.getInstance().getReference(DBKey.myNotification.toString())
        this.REF_EXIST_NOTIFICATION = FirebaseDatabase.getInstance().getReference(DBKey.existNotification.toString())
    }

    fun observeMyNotifications(uid: String, completion: (Notification?, Int) -> Unit) {
        REF_MYNOTIFICATION.child(uid).orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (child in snapshot.children) {

                        observeNotification(child.key!!, {
                            completion(it, snapshot.childrenCount.toInt())
                        })
                    }

                }
            })

    }

    fun observeNotification(id: String, completion: (Notification?) -> Unit) {

        val query = REF_NOTIFICATION.child(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val notification = Notification.transformNotification(it, snapshot.key)
                    completion(notification)
                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }

    fun observeExistNotification(uid: String, postId: String, type: String, currentUid: String, completion: (String?) -> Unit) {

        REF_EXIST_NOTIFICATION.child(uid).child(currentUid).child(postId).child(type).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snapshot: DataSnapshot?) {

                val arraySnapshot = snapshot?.children

                var notificationId: String? = null

                arraySnapshot?.let {
                    for (child in it) {
                        notificationId = child.key
                        completion(notificationId)
                    }

                }

                if (notificationId == null) {
                    completion(null)
                }
            }
        })
    }

    fun sendNotification(token: String, message: String, onSuccess: () -> Unit, onError: () -> Unit) {

        val urlString = "https://fcm.googleapis.com/fcm/send"
        val url: URL = URL(urlString)
        try {
            val conn = url.openConnection() as HttpsURLConnection
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "key=$firebaseServerKey")

            Log.d("TAG", token)

            val str = "{\"to\": \"$token\", \"notification\": { \"body\": \"$message\", \"badge\": \"1\"}}"


            val outputInBytes = str.toByteArray(charset("UTF-8"))
            val os = conn.outputStream
            os.write(outputInBytes)
            os.close()

            val responseCode = conn.responseCode

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Log.d("Success", responseCode.toString())
                onSuccess()
            } else {
                val code = responseCode.toString()
                val message2 = conn.responseMessage
                Log.d("Fail", responseCode.toString())
                Log.d("Fail2", conn.responseMessage)
                onError()
            }

        } catch (e: Exception) {
            onError()
            e.printStackTrace()
        }

    }
}


