package jp.bazari.Apis

import android.util.Log
import com.google.firebase.database.*
import jp.bazari.models.ValueModel

class ValueApi {

    lateinit var REF_VALUE: DatabaseReference
    lateinit var REF_MYVALUE: DatabaseReference


    constructor() {
        this.REF_VALUE = FirebaseDatabase.getInstance().getReference(DBKey.value.toString())
        this.REF_MYVALUE = FirebaseDatabase.getInstance().getReference(DBKey.myValue.toString())
    }

    fun observeValue(id: String, completion: (ValueModel?) -> Unit) {
        REF_VALUE.child(id).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(dataError: DatabaseError) {
                Log.d("",dataError.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val dict = snapshot.value as HashMap<String, Any>?

                dict?.let {
                    val value = ValueModel.transformValue(it, snapshot.key)
                    completion(value)
                }

                if (dict == null) {
                    completion(null)
                }
            }
        })
    }

    fun fetchMyValues(uid: String, completion: (ValueModel?, Int) -> Unit) {

        REF_MYVALUE.child(uid).orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("snapshotKey ", snapshot.childrenCount.toInt().toString())

                val dsChild = snapshot.children

                for (child in snapshot.children) {
                    Log.d("ds0Key ", child.key)

                    Api.Value.observeValue(child.key, {

                        it?.let {
                            completion(it, snapshot.childrenCount.toInt())
                        }
                    })
                }

            }
        })
    }
}