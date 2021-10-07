package jp.bazari.models

import java.io.Serializable
import java.util.*

class CardModel : Serializable {

    var id: String? = null
    var cardType: String? = null
    var deletedFlg: Boolean? = null

    companion object {

        fun transformCard(dict: HashMap<String, Any>, key: String): CardModel {

            val card = CardModel()
            card.id = key
            card.cardType = dict["cardType"] as? String
            card.deletedFlg = dict["deletedFlg"] as? Boolean
            return card
        }
    }
}