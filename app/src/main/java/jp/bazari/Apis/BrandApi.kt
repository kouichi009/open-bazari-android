package jp.bazari.Apis

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BrandApi {

    lateinit var REF_BRAND_CANDIDATES: DatabaseReference

    constructor() {
        this.REF_BRAND_CANDIDATES = FirebaseDatabase.getInstance().getReference(DBKey.brandCandidates.toString())
    }
}