package jp.bazari.models

import java.util.*

class Comment {

    var id: String? = null
    var commentText: String? = null
    var uid: String? = null
    var postId: String? = null
    var timestamp: Long? = null
    var isCancel: Boolean? = null


    constructor()

    companion object {

        fun transformComment(dict: HashMap<String, Any>, key: String): Comment {

            val comment = Comment()
            comment.id = key
            comment.postId = dict["postId"] as? String
            comment.commentText = dict["commentText"] as? String
            comment.uid = dict["uid"] as? String
            comment.timestamp = dict["timestamp"] as? Long
            comment.isCancel = dict["isCancel"] as? Boolean

            return comment
        }
    }
}