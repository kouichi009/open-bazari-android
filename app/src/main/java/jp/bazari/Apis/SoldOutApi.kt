package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post

class SoldOutApi {

    lateinit var REF_MY_SOLDOUT_POSTS: DatabaseReference
    lateinit var REF_SOLDOUT_POSTS: DatabaseReference

    constructor() {
        this.REF_MY_SOLDOUT_POSTS = FirebaseDatabase.getInstance().getReference(DBKey.mySoldOutPosts.toString())
        this.REF_SOLDOUT_POSTS = FirebaseDatabase.getInstance().getReference(DBKey.soldOutPosts.toString())
    }

    fun observeMyPosts(uid: String, completion: (Post?, Int) -> Unit) {
        REF_MY_SOLDOUT_POSTS.child(uid).orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("snapshotKey ", snapshot.childrenCount.toInt().toString())

                    val dsChild = snapshot.children

                    for (child in snapshot.children) {
                        Log.d("ds0Key ", child.key)
                        observeSoldOutPost(child.key!!, {

                            completion(it, snapshot.childrenCount.toInt())
                        })
                    }

                }
            })

    }

    fun observeSoldOutPost(id: String, completion: (Post?) -> Unit) {
        val query = REF_SOLDOUT_POSTS.child(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val post = Post.transformPost(it, snapshot.key!!)
                    completion(post)
                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }
}