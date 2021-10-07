package jp.bazari.models

import java.util.*

class Address {

    var id: String? = null
    var seiKanji: String? = null
    var meiKanji: String? = null
    var seiKana: String? = null
    var meiKana: String? = null
    var phoneNumber: String? = null
    var postalCode: String? = null
    var prefecure: String? = null
    var city: String? = null
    var tyou: String? = null
    var building: String? = null
    var creditcard_Exist: Boolean? = null

    companion object {

        fun transformAddress(dict: HashMap<String, Any>, key: String): Address {

            val address = Address()
            address.id = key
            address.seiKanji = dict["seiKanji"] as? String
            address.meiKanji = dict["meiKanji"] as? String
            address.seiKana = dict["seiKana"] as? String
            address.meiKana = dict["meiKana"] as? String
            address.phoneNumber = dict["phoneNumber"] as? String
            address.postalCode = dict["postalCode"] as? String
            address.prefecure = dict["prefecture"] as? String
            address.city = dict["city"] as? String
            address.tyou = dict["tyou"] as? String
            address.building = dict["building"] as? String
            address.creditcard_Exist = dict["creditcard_Exist"] as? Boolean
            return address
        }
    }
}