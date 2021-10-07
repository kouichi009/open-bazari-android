package jp.bazari.Apis

import com.google.firebase.database.*
import jp.bazari.models.BankInfo

class BankInfoApi {

    lateinit var REF_MYBANK: DatabaseReference

    constructor() {
        this.REF_MYBANK = FirebaseDatabase.getInstance().getReference(DBKey.myBank.toString())
    }

    fun observeMyBank(userId: String, completion: (BankInfo?) -> Unit) {

        REF_MYBANK.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                    val dict = snapshot.value as HashMap<String, Any>?

                    dict?.let {
                        val bank = BankInfo.transformBank(it, snapshot.key)
                        completion(bank)
                    }

                    if (dict == null) {
                        completion(null)
                    }
            }
        })
    }
}