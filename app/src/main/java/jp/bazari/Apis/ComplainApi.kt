package jp.bazari.Apis

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ComplainApi {

    lateinit var REF_COMPLAINS: DatabaseReference

    constructor() {
        this.REF_COMPLAINS = FirebaseDatabase.getInstance().getReference(DBKey.complains.toString())

    }
}