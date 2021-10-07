package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post


class MyPurchasePostsApi {

    lateinit var REF_MYPURCHASE: DatabaseReference

    constructor() {

        this.REF_MYPURCHASE = FirebaseDatabase.getInstance().getReference(DBKey.myPurchasePosts.toString())
    }

    fun observeMyPurchasePosts(uid: String, completion: (Post?, Int) -> Unit) {

        val query = REF_MYPURCHASE.child(uid)

        query.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.value == null) {
                    completion(null, 0)
                    return
                }

                for (child in dataSnapshot.children) {
                    Log.d("ds0Key ", child.key)
                    Api.Post.observePost(child.key!!, {

                        completion(it, dataSnapshot.childrenCount.toInt())
                    })
                }
            }
        })
    }
}