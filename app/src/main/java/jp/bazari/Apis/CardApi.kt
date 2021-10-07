package jp.bazari.Apis

import com.google.firebase.database.*
import jp.bazari.models.CardModel

class CardApi {

    lateinit var REF_CARDS: DatabaseReference
    constructor() {
        this.REF_CARDS = FirebaseDatabase.getInstance().getReference(DBKey.cards.toString())
    }

    fun observeCard(id: String, completion: (CardModel?, Int) -> Unit) {

        REF_CARDS.child(id).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.value == null) {
                    completion(null, 0)
                    return
                }

                for (child in snapshot.children) {

                    val dict = child.value as HashMap<String, Any>?

                    dict?.let {
                        val card = CardModel.transformCard(it, child.key)
                        completion(card, snapshot.childrenCount.toInt())
                    }

                    if (dict == null) {
                        completion(null, 0)
                    }
                }
            }
        })
    }
}