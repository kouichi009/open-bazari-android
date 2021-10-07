package jp.bazari.models

import java.util.*

class BankTransDate {

    var accountNumber: Long? = null
    var accountType: String? = null
    var bank: String? = null
    var branchCode: Long? = null
    var firstName: String? = null
    var lastName: String? = null
    var price: Long? = null
    var applyDate: String? = null

    companion object {

        fun transformBankTrans(dict: HashMap<String, Any>, key: String): BankTransDate {

            val bankTrans = BankTransDate()
            bankTrans.accountNumber = dict["accountNumber"] as? Long
            bankTrans.accountType = dict["accountType"] as? String
            bankTrans.bank = dict["bank"] as? String
            bankTrans.branchCode = dict["branchCode"] as? Long
            bankTrans.firstName = dict["firstName"] as? String
            bankTrans.lastName = dict["lastName"] as? String
            bankTrans.price = dict["price"] as? Long
            bankTrans.applyDate = dict["applyDate"] as? String
            return bankTrans
        }
    }
}