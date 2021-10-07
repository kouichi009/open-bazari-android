package jp.bazari.models

import java.util.*

class Charge {

    var id: String? = null
    var postId: String? = null
    var timestamp: Long? = null
    var titleStr: String? = null
    var price: Int? = null
    var type: String? = null

    companion object {

        fun transformCharge(dict: HashMap<String, Any>, key: String): Charge {

            val charge = Charge()
            charge.id = key
            charge.postId = dict["postId"] as? String
            charge.timestamp = dict["timestamp"] as? Long
            charge.titleStr = dict["title"] as? String
            val price = dict["price"] as? Long
            price?.let {
                charge.price = it.toInt()
            }
            charge.type = dict["type"] as? String
            return charge
        }
    }
}