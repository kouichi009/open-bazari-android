package jp.bazari.Activities

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ligl.android.widget.iosdialog.IOSDialog
import jp.bazari.Adapters.CommentsAdapter
import jp.bazari.Apis.*
import jp.bazari.R
import jp.bazari.models.Comment
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*


class CommentsActivity : AppCompatActivity() {

    var comments = ArrayList<Comment>()
    var commentsAdapter: CommentsAdapter? = null

    var mPost = Post()

    var currentUid: String? = null

    var mUsermodels = ArrayList<UserModel>()
    var mUsermodel: UserModel? = null

    var mSelectedUserModels = ArrayList<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val bundle = intent.extras
        mPost = bundle.getSerializable(IntentKey.POST.name) as Post

        optionTv.text = "コメント"
        backArrow.setOnClickListener {
            finish()
        }

        currentUid = getCurrentUser()!!.uid

        sendIv.setOnClickListener {
            sendComemnt(currentUid!!)

        }

        fetchUsers()
    }

    fun fetchUsers() {
        mUsermodels.clear()
        var count = 0

        Api.User.observeUsers({ usermodel, usersCount ->

            count++

            usermodel?.let {
                this.mUsermodels.add(it)

                if(mPost.uid!! == it.id!!) {
                    this.mUsermodel = it
                }
            }

            if (count == usersCount) {
                loadComments()

            }
        })
    }

    fun sendComemnt(currentUid: String) {

        val commentTex = commentEdit.text.toString()

        if (commentTex.equals("")) {
            IOSDialog.Builder(this)
                .setMessage("コメントが空白です。")
                .setPositiveButton("OK", null).show()
            return
        }

        val autoKey = Api.Comment.REF_COMMENTS.push().key
        Api.Comment.REF_POST_COMMENTS.child(mPost.id!!).child(autoKey!!).child(getString(R.string.timestamp)).setValue(getTimestamp())

        var result = HashMap<String, Any>()
        result.put(getString(R.string.commentText), commentTex)
        result.put("postId", (mPost.id!!))
        result.put(getString(R.string.timestamp), getTimestamp())
        result.put("uid", getCurrentUser()!!.uid)
        Api.Comment.REF_COMMENTS.child(autoKey!!).setValue(result)

        var result2 = HashMap<String, Any>()
        result2.put(getCurrentUser()!!.uid, true)

        var result3 = HashMap<String, Any>()
        result3.put("commentCount", comments.count() + 1)

        Api.Post.REF_POSTS.child(mPost.id!!).child("comments").updateChildren(result2)
        Api.Post.REF_POSTS.child(mPost.id!!).updateChildren(result3)

        Api.MyCommentPosts.REF_MYCOMMENTING_POSTS.child(currentUid).child(mPost.id!!).child(getString(R.string.timestamp)).setValue(
            getTimestamp())

        setNotificationDB(autoKey)


        val comment = Comment.transformComment(result, autoKey!!)

        addComment(comment)
        clean()
    }

    fun setNotificationDB(commentId: String) {

        if (mPost.uid!! != currentUid ) {

            val fcmToken = mUsermodel?.fcmToken
            fcmToken?.let {

                AsyncTask.execute {  sendPushNotification(it) }

            }

            Api.Notification.observeExistNotification(mPost.uid!!, mPost.id!!, "comment", currentUid!!, {

                val newNotificationReference = Api.Notification.REF_NOTIFICATION
                val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(mPost.uid!!)
                val newNotificationId = newNotificationReference.push().key
                val timestamp = getTimestamp()

                //notificaionIdがnullであれば
                if (it == null) {

                    var result = HashMap<String, Any>()
                    result.put("checked", false)
                    result.put("from", currentUid!!)
                    result.put("objectId", mPost.id!!)
                    result.put("type", "comment")
                    result.put("timestamp", timestamp)
                    result.put("to", mPost.uid!!)
                    result.put("commentId", commentId)
                    result.put("segmentType", "you")

                    newNotificationReference.child(newNotificationId!!).setValue(result)

                    newMyNotificationReference.child(newNotificationId).child(getString(R.string.timestamp))
                        .setValue(timestamp)

                    Api.Notification.REF_EXIST_NOTIFICATION.child(mPost.uid!!).child(currentUid!!).child(mPost.id!!)
                        .child("comment").child(newNotificationId).setValue(true)

                } else {
                    newMyNotificationReference.child(it!!).child(getString(R.string.timestamp)).setValue(timestamp)
                }
            })

            //商品投稿者がコメントしたときだけ、商品にコメントを残した人たちに通知を送る
        } else {
            val withUserPostComments = mPost.comments
            withUserPostComments?.let {
                for(withUserComment in it) {
                    val withUserCommentUid = withUserComment.key

                    if (withUserCommentUid != currentUid!!) {
                        Api.Notification.observeExistNotification(withUserCommentUid, mPost.id!!, "comment", currentUid!!, {

                            val timestamp = getTimestamp()
                            val newNotificationReference = Api.Notification.REF_NOTIFICATION
                            val newMyNotificationReference = Api.Notification.REF_MYNOTIFICATION.child(withUserCommentUid)
                            val newNotificationId = newNotificationReference.push().key

                            if(it == null) {
                                var result = HashMap<String, Any>()
                                result.put("checked", false)
                                result.put("from", currentUid!!)
                                result.put("objectId", mPost.id!!)
                                result.put("type", "comment")
                                result.put("timestamp", timestamp)
                                result.put("to", withUserCommentUid)
                                result.put("commentId", commentId)
                                result.put("segmentType", "you")

                                newNotificationReference.child(newNotificationId).setValue(result)
                                newMyNotificationReference.child(newNotificationId).child(getString(R.string.timestamp)).setValue(timestamp)

                                Api.Notification.REF_EXIST_NOTIFICATION.child(withUserCommentUid)
                                    .child(currentUid).child(mPost.id!!).child("comment").child(newNotificationId).setValue(true)

                            } else {
                                newMyNotificationReference.child(it).child(getString(R.string.timestamp)).setValue(timestamp)
                            }
                        })
                    }
                }
            }
        }
    }

    fun addComment(comment: Comment) {

        mUsermodels.forEach {
            if(it.id == comment.uid) {
                mSelectedUserModels.add(0, it)
                comments.add(0,comment)
            }
        }

        //コメントが一個もなかった場合は、adapterをセットする必要がある。
        //はじめてコメントしたら、commentsのsizeが１になるので、1であれば、アダプターをセットする。
        if (comments.size <= 1) {
            commentsAdapter = CommentsAdapter(this, R.layout.comment_list_content, comments, mSelectedUserModels, mPost)
            listView.adapter = commentsAdapter
            return
        }

        commentsAdapter?.notifyDataSetChanged()
    }

    fun loadComments() {
        var count = 0
        comments.clear()

        Api.Comment.observePostComments(mPost.id!!, { comment, commentCount ->

            if (commentCount == 0) {
                return@observePostComments
            }
            count++

            comment?.let {comment ->

                mUsermodels.forEach { usermodel ->

                    if (usermodel.id == comment.uid) {
                        mSelectedUserModels.add(0, usermodel)
                        comments.add(0, comment)
                    }
                }
            }

            if (count == commentCount) {
                commentsAdapter = CommentsAdapter(this, R.layout.comment_list_content, comments, mSelectedUserModels, mPost)
                listView.adapter = commentsAdapter
            }
        })
    }

    fun clean() {
        commentEdit.setText("")
    }

    fun sendPushNotification(token: String) {

        //表示タイトルの文字数の限界を設定
        val indexNum = pushNotiTitleCount
        var titleStr: String = mPost.title!!

        if (titleStr.count() > indexNum) {
            titleStr = titleStr.substring(0, indexNum)
            titleStr = titleStr+"..."
        }
        val message = "「" + titleStr + "に新着コメントがあります。"


        Api.Notification.sendNotification(token, message, {

            //onSuccess

        },{

            //onError


        })

    }
}