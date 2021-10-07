package jp.bazari.StripeUtils

import android.util.Log
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import jp.bazari.Apis.StripeSecretKeyForServer

object StripeTools {

    private var stripeSecret = StripeSecretKeyForServer

    fun generateToken(card: Card, stripe: Stripe, completion: (Token?) -> Unit) {

        stripe.createToken(card, object : TokenCallback {
            override fun onError(error: Exception) {
                Log.d("error Message", error.localizedMessage)
                completion(null)
            }

            override fun onSuccess(token: Token) {
                Log.d("Success!!", token.toString())

                completion(token)
            }
        })
    }

    fun getBasicAuth() : String {

        return "Bearer "+stripeSecret
    }
}