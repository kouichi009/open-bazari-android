package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post

class MyCommentingPostsApi {

    lateinit var REF_MYCOMMENTING_POSTS: DatabaseReference

    constructor() {
        this.REF_MYCOMMENTING_POSTS = FirebaseDatabase.getInstance().getReference(DBKey.myCommentingPosts.toString())
    }

    fun observeMyCommentPosts(uid: String, completion: (Post?, Int) -> Unit) {

        REF_MYCOMMENTING_POSTS.child(uid).orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("snapshotKey ", snapshot.childrenCount.toInt().toString())

                    val dsChild = snapshot.children

                    for (child in snapshot.children) {
                        Log.d("ds0Key ", child.key)
                        Api.Post.observePost(child.key!!, {

                            completion(it, snapshot.childrenCount.toInt())
                        })
                    }

                }
            })

    }

    fun deleteMyCommentingPost(postId: String) {

        REF_MYCOMMENTING_POSTS.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(dataSnapError: DatabaseError) {
                Log.d("tag", dataSnapError.details)
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (child in snapshot.children) {
                    val userId = child.key
                    REF_MYCOMMENTING_POSTS.child(userId).child(postId).removeValue()
                }
            }
        })
    }
}