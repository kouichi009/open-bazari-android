package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Comment

class CommentApi {

    lateinit var REF_COMMENTS: DatabaseReference
    lateinit var REF_POST_COMMENTS: DatabaseReference

    constructor()  {
        this.REF_COMMENTS = FirebaseDatabase.getInstance().getReference(DBKey.comments.toString())
        this.REF_POST_COMMENTS = FirebaseDatabase.getInstance().getReference("post-comments")
    }


    fun observePostComments(postId: String, completion: (Comment?, Int) -> Unit) {

        val query = REF_POST_COMMENTS.child(postId)
        query.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("snapshotKey " + snapshot.childrenCount.toInt(), snapshot.key)

                if (snapshot.childrenCount.toInt() == 0) {
                    completion(null, 0)
                    return
                }

                for (child in snapshot.children) {
                    observeComment(child.key!!, {
                        completion(it, snapshot.childrenCount.toInt())
                    })
                }
            }

        })
    }

    fun observeComment(commentId: String, completion: (Comment?) -> Unit) {
        val query = REF_COMMENTS.child(commentId)
        query.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(dataSnapError: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val comment = Comment.transformComment(it, snapshot.key!!)
                        completion(comment)

                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }

    fun deleteComments(postId: String) {

        REF_POST_COMMENTS.child(postId).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(dataSnapError: DatabaseError) {
                Log.d("tag", dataSnapError.details)
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (child in snapshot.children) {
                    val commentId = child.key
                    REF_COMMENTS.child(commentId).removeValue()
                    REF_POST_COMMENTS.child(postId).child(commentId).removeValue()
                }
            }
        })
    }
}