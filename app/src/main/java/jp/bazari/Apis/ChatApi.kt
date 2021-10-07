package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Chat

class ChatApi {

    lateinit var REF_CHATS: DatabaseReference

    constructor() {
        this.REF_CHATS = FirebaseDatabase.getInstance().getReference(DBKey.chats.toString())
    }

    fun observeChats(id: String, completion: (Chat?, Int) -> Unit) {
        Log.d("id", id)
        val query = REF_CHATS.child(id)
        //  val query2 = REF_POSTS.child("")
        Log.d("query", query.key)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("snapshotKey ", snapshot.childrenCount.toInt().toString())


                if (!snapshot.exists()) {
                    completion(null, 0)
                    return
                }

                for (data in snapshot.children) {

                    Log.d("TAG", data.key)

                    val dict = data.value as HashMap<String, Any>?

                    dict?.let {
                        val chat = Chat.transformChat(it, data!!.key!!)
                        completion(chat, snapshot.childrenCount.toInt())
                    }

                    if (dict == null) {
                        completion(null, 0)
                    }
                }
            }

        })
    }
}