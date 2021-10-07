package jp.bazari.models

import java.util.*

class BankInfo {

    var id: String? = null
    var accountNumber: Long? = null
    var accountType: String? = null
    var bank: String? = null
    var branchCode: Long? = null
    var firstName: String? = null
    var lastName: String? = null

    companion object {

        fun transformBank(dict: HashMap<String, Any>, key: String): BankInfo {

            val bankInfo = BankInfo()
            bankInfo.id = key
            bankInfo.accountNumber = dict["accountNumber"] as? Long
            bankInfo.accountType = dict["accountType"] as? String
            bankInfo.bank = dict["bank"] as? String
            bankInfo.branchCode = dict["branchCode"] as? Long
            bankInfo.firstName = dict["firstName"] as? String
            bankInfo.lastName = dict["lastName"] as? String
            return bankInfo
        }
    }
}