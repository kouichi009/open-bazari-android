package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.UserModel

class UserApi {

    lateinit var REF_USERS: DatabaseReference


    constructor() {
        this.REF_USERS = FirebaseDatabase.getInstance().getReference(DBKey.users.toString())
    }

    fun observeUsers(completion: (UserModel?, Int) -> Unit) {
        REF_USERS.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(dataError: DatabaseError) {
                Log.d("", dataError.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    completion(null, 0)
                    return
                }

                for (data in dataSnapshot.children) {

                    val dict = data.value as HashMap<String, Any>?

                    dict?.let {
                        val usermodel = UserModel.transformUser(it, data.key!!)

                            completion(usermodel, dataSnapshot.childrenCount.toInt())

                    }

                    if (dict == null) {
                        completion(null, 0)
                    }
                }
            }
        })
    }

    fun observeUser(id: String, completion: (UserModel?) -> Unit) {
        val query = REF_USERS.child(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {

                    val usermodel = UserModel.transformUser(it, snapshot.key)
                        completion(usermodel)

                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }
}