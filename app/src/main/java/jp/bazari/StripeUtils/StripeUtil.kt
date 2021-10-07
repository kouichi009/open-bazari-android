package jp.bazari.StripeUtils

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object StripeUtil {

    fun createUser(context: Context, stripe: Stripe, card: Card, uidEmail: String, completion: (Boolean) -> Unit) {

        val queue = Volley.newRequestQueue(context)
        val url = "https://api.stripe.com/v1/customers"
        val jsonObjRequest = object : StringRequest(Request.Method.POST, url,

            Response.Listener { response ->
                try {

                    val jsonBody = JSONObject(response)

                    try {
                        val customerId = jsonBody.getString("id")
                        createCard(context,stripe,customerId, card, completion)
                    } catch (e: Exception) {
                        completion(false)
                        e.printStackTrace()
                    }


                } catch (ex: JSONException) {
                    completion(false)
                    ex.printStackTrace()

                }
            },
            Response.ErrorListener {
                completion(false)

            }) {

            override fun getBodyContentType(): String {

                return "application/x-www-form-urlencoded; charset=UTF-8"

            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {

                val params = HashMap<String, String>()

                params["email"] = uidEmail

                return params

            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()

                headers["Authorization"] = StripeTools.getBasicAuth()

                return headers

            }

        }

        queue.add(jsonObjRequest)
    }

    fun createCard(context: Context, stripe: Stripe, customerId: String, card: Card, completion: (Boolean) -> Unit) {

        stripe.createToken(card, object : TokenCallback {
            override fun onError(error: Exception) {
                error.printStackTrace()
                completion(false)

            }

            override fun onSuccess(token: Token?) {

                if (token != null) {
                    val url = "https://api.stripe.com/v1/customers/$customerId/sources"
                    val queue = Volley.newRequestQueue(context)
                    val jsonObjRequest = object : StringRequest(Request.Method.POST, url,

                        Response.Listener { response ->
                            try {

                               // val jsonBody = JSONObject(response)
                                completion(true)

                            } catch (ex: JSONException) {
                                ex.printStackTrace()
                                completion(false)


                            }
                        }, Response.ErrorListener { error ->
                            error.printStackTrace()
                            completion(false)

                        }) {

                        override fun getBodyContentType(): String {

                            return "application/x-www-form-urlencoded; charset=UTF-8"

                        }

                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {

                            val params = HashMap<String, String>()
                            params["source"] = token.id

                            return params

                        }

                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {

                            val headers = HashMap<String, String>()

                            headers["Authorization"] = StripeTools.getBasicAuth()

                            return headers

                        }

                    }

                    queue.add(jsonObjRequest)
                }

            }
        })
    }

    fun getUserList(context: Context, uidEmail: String, completion: (JSONArray?) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "https://api.stripe.com/v1/customers?email=$uidEmail"

        val jsonObjRequest = object : StringRequest(Request.Method.GET, url,

            Response.Listener { response ->
                try {

                    val jsonBody = JSONObject(response)

                    try {
                        val customersArray = jsonBody.getJSONArray("data") as JSONArray
                        completion(customersArray)

                    } catch (e: Exception) {
                        completion(null)
                        e.printStackTrace()
                    }

                } catch (ex: JSONException) {
                    completion(null)
                    ex.printStackTrace()
                }
            },
            Response.ErrorListener {
                completion(null)

            }) {

            override fun getBodyContentType(): String {

                return "application/x-www-form-urlencoded; charset=UTF-8"

            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {

                val params = HashMap<String, String>()

                params["email"] = uidEmail

                return params

            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()

                headers["Authorization"] = StripeTools.getBasicAuth()

                return headers

            }

        }

        queue.add(jsonObjRequest)
    }

    fun getCardsList(context: Context, customerId: String, completion: (JSONArray?) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val url = "https://api.stripe.com/v1/customers/$customerId/sources?object=card"

        val jsonObjRequest = object : StringRequest(Request.Method.GET, url,

            Response.Listener { response ->
                try {

                    val jsonBody = JSONObject(response)

                    try {
                        val cardsArray = jsonBody.getJSONArray("data") as JSONArray
                        completion(cardsArray)

                    } catch (e: Exception) {
                        completion(null)
                        e.printStackTrace()
                    }

                } catch (ex: JSONException) {
                    completion(null)
                    ex.printStackTrace()
                }
            },
            Response.ErrorListener {
                completion(null)

            }) {

            override fun getBodyContentType(): String {

                return "application/x-www-form-urlencoded; charset=UTF-8"

            }


            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()

                headers["Authorization"] = StripeTools.getBasicAuth()

                return headers

            }

        }

        queue.add(jsonObjRequest)
    }
}