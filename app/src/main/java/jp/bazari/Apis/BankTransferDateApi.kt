package jp.bazari.Apis

import com.google.firebase.database.*
import jp.bazari.models.BankTransDate

class BankTransferDateApi {

    lateinit var REF_BANK_TRANSFER_DATES: DatabaseReference


    constructor() {
        this.REF_BANK_TRANSFER_DATES = FirebaseDatabase.getInstance().getReference(DBKey.bankTransferDates.toString())
    }

    fun observeBankTrasDate(year: String, month_date: String, uid: String, completion: (BankTransDate?) -> Unit) {

        REF_BANK_TRANSFER_DATES.child(year).child(month_date).child(uid).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val bankTransDate = BankTransDate.transformBankTrans(it, snapshot.key)
                    completion(bankTransDate)
                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }

}