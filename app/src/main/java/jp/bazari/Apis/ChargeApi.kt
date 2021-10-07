package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.Charge

class ChargeApi {

    lateinit var REF_MYCHARGE: DatabaseReference


    constructor() {
        this.REF_MYCHARGE = FirebaseDatabase.getInstance().getReference(DBKey.myCharge.toString())
    }

    fun observeMyCharges(uid: String, completion: (Charge?, Int) -> Unit) {

        REF_MYCHARGE.child(uid).orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(dataError: DatabaseError) {
                Log.d("",dataError.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    completion(null, 0)
                    return
                }

                for (data in dataSnapshot.children) {

                    val dict = data.value as HashMap<String, Any>?

                    dict?.let {
                        val charge = Charge.transformCharge(it, data.key!!)
                        completion(charge, dataSnapshot.childrenCount.toInt())
                    }

                    if (dict == null) {
                        completion(null, 0)
                    }
                }
            }
        })
    }
}