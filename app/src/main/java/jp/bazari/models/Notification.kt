package jp.bazari.models

import java.io.Serializable
import java.util.*

class Notification : Serializable {

    var from: String? = null
    var to: String? = null
    var objectId: String? = null
    var commentId: String? = null
    var type: String? = null
    var timestamp: Long? = null
    var id: String? = null
    var checked: Boolean? = null
    var segmentType: String? = null

    constructor()

    companion object {

        fun transformNotification(dict: HashMap<String, Any>, key: String): Notification {

            val notification = Notification()
            notification.id = key
            notification.objectId = dict["objectId"] as? String
            notification.type = dict["type"] as? String
            notification.from = dict["from"] as? String
            notification.to = dict["to"] as? String
            notification.checked = dict["checked"] as? Boolean
            notification.commentId = dict["commentId"] as? String
            notification.segmentType = dict["segmentType"] as? String
            notification.timestamp = dict["timestamp"] as? Long

            return notification
        }
    }
}