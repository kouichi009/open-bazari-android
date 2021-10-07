package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Post


class PostApi {

    lateinit var REF_POSTS: DatabaseReference

    constructor() {
        this.REF_POSTS = FirebaseDatabase.getInstance().getReference(DBKey.posts.toString())
    }

    fun fetchCountPosts(completion: (Int) -> Unit) {

        REF_POSTS.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(snapshot: DatabaseError?) {

            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val count = snapshot!!.childrenCount.toInt()
                completion(snapshot?.childrenCount!!.toInt())
            }
        })
    }

    fun observePostsAdded(completion: (Post?, String) -> Unit) {
        REF_POSTS.orderByChild("timestamp").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot?, key: String?) {

                val dict = snapshot!!.value as HashMap<String, Any>?

                dict?.let {
                    val post = Post.transformPost(it, snapshot.key!!)

                        completion(post, "add")

                }

                if (dict == null) {
                    completion(null, "add")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot?, key: String?) {

                val dict = snapshot!!.value as HashMap<String, Any>?

                dict?.let {
                    val post = Post.transformPost(it, snapshot.key!!)
                    completion(post, "change")

                }

                if (dict == null) {
                    completion(null, "change")
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot?) {

                val dict = snapshot!!.value as HashMap<String, Any>?

                dict?.let {
                    val post = Post.transformPost(it, snapshot.key!!)
                    completion(post, "remove")
                }

                if (dict == null) {
                    completion(null, "remove")
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot?, key: String?) {
                val sValue = snapshot?.value

            }

            override fun onCancelled(error: DatabaseError?) {
                val error = error?.details

            }

        })
    }



    fun observePosts(page: Int, completion: (Post?, Int) -> Unit) {
        REF_POSTS.orderByChild("timestamp").limitToLast(page).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(dataError: DatabaseError) {
                Log.d("", dataError.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    completion(null, 0)
                    return
                }

                for (data in dataSnapshot.children.reversed()) {

                    val dict = data.value as HashMap<String, Any>?

                    dict?.let {
                        val post = Post.transformPost(it, data.key!!)
                            completion(post, dataSnapshot.childrenCount.toInt())

                    }

                    if (dict == null) {
                        completion(null, 0)
                    }
                }
            }
        })
    }

    fun observePost(id: String, completion: (Post?) -> Unit) {
        val query = REF_POSTS.child(id)
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

    fun incrementLikes(postId: String, currentUid: String, onSuccess: (Post) -> Unit, onError: (String?) -> Unit) {

        val postRef = Api.Post.REF_POSTS.child(postId)

        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                val post = mutableData.value as HashMap<String, Any>

                var likes = post["likes"] as HashMap<String, Boolean>?
                if (likes == null) {
                    //初期化（nullでなくなる。）
                    likes = HashMap()
                }

                var likeCount = post["likeCount"] as Long?
                if (likeCount == null) {
                    //初期化（nullでなくなる。）
                    likeCount = 0
                }

                if (likes[currentUid] == true) {
                    likeCount -= 1
                    likes.remove(currentUid)
                } else {
                    likeCount += 1
                    likes[currentUid] = true
                }

                post["likeCount"] = likeCount.toInt()
                post["likes"] = likes

                mutableData.value = post

                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?, boolean: Boolean,
                dataSnapshot: DataSnapshot?
            ) {

                // Transaction completed
                if (databaseError != null) {
                    onError(databaseError!!.message)
                    return
                }


                val dict = dataSnapshot!!.value as HashMap<String, Any>
                val post = Post.transformPost(dict, dataSnapshot!!.key!!)
                onSuccess(post)

            }

        })
    }
}