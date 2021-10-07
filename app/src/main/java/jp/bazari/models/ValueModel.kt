package jp.bazari.models

class ValueModel {

    var id: String? = null
    var postId: String? = null
    var valueStatus: String? = null
    var valueComment: String? = null
    var from: String? = null
    var type: String? = null
    var timestamp: Long? = null

    constructor()

    companion object {

        fun transformValue(dict: HashMap<String, Any>, key: String): ValueModel {

            val value = ValueModel()
            value.id = key
            value.valueStatus = dict["valueStatus"] as? String
            value.valueComment = dict["valueComment"] as? String
            value.postId = dict["postId"] as? String
            value.from = dict["from"] as? String
            value.type = dict["type"] as? String
            value.timestamp = dict["timestamp"] as? Long
            return value
        }
    }
}