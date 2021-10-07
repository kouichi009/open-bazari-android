package jp.bazari.models

import java.util.*

class Chat {

    var id: String? = null
    var messageText: String? = null
    var postId: String? = null
    var timestamp: Long? = null
    var date: String? = null
    var uid: String? = null

    constructor()

    companion object {

        fun transformChat(dict: HashMap<String, Any>, key: String): Chat {

            val chat = Chat()
            chat.messageText = dict["messageText"] as? String
            chat.id = key
            chat.postId = dict["postId"] as? String
            chat.timestamp = dict["timestamp"] as? Long
            chat.date = dict["date"] as? String
            chat.uid = dict["uid"] as? String

            return chat
        }
    }

}