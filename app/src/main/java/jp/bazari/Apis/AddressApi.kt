package jp.bazari.Apis

import com.google.firebase.database.*
import jp.bazari.models.Address

class AddressApi {

    lateinit var REF_ADDRESS: DatabaseReference

    constructor() {
        this.REF_ADDRESS = FirebaseDatabase.getInstance().getReference(DBKey.address.toString())
    }

    fun observeAddress(userId: String, completion: (Address?) -> Unit) {

        REF_ADDRESS.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val address = Address.transformAddress(it, snapshot.key)
                    completion(address)
                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }
}