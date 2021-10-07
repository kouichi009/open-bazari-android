package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post

class MyPostsApi {

    lateinit var REF_MYPOSTS: DatabaseReference


    constructor() {
        this.REF_MYPOSTS = FirebaseDatabase.getInstance().getReference(DBKey.myPosts.toString())
    }

    fun observeMyPosts(uid: String, completion: (Post?, Int) -> Unit) {
        REF_MYPOSTS.child(uid).orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {

                    completion(null, 0)
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.childrenCount.toInt() == 0) {
                        completion(null, 0)
                        return
                    }

                    for (child in snapshot.children) {
                        Log.d("ds0Key ", child.key)
                        Api.Post.observePost(child.key!!, {

                            completion(it, snapshot.childrenCount.toInt())
                        })
                    }

                }
        })

    }


}