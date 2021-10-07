package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post

class MyLikingPostsApi {

    lateinit var REF_MYLIKING_POSTS: DatabaseReference

    constructor() {
        this.REF_MYLIKING_POSTS = FirebaseDatabase.getInstance().getReference(DBKey.myLikingPosts.toString())
    }

    fun observeMyLikePosts(uid: String, completion: (Post?, Int) -> Unit) {
        REF_MYLIKING_POSTS.child(uid).orderByChild("timestamp")
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

    fun deleteMyLikingPost(postId: String) {

        REF_MYLIKING_POSTS.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(dataSnapError: DatabaseError) {
                Log.d("tag", dataSnapError.details)
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (child in snapshot.children) {
                    val userId = child.key

                    if(child.hasChild(postId)) {
                        REF_MYLIKING_POSTS.child(userId).child(postId).removeValue()
                    }
                }
            }
        })
    }
}