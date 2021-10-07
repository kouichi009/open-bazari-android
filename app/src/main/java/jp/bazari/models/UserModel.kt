package jp.bazari.models

import jp.bazari.Apis.SELF_INTRODUCTION
import java.io.Serializable

class UserModel : Serializable {

    var email: String? = null
    var profileImageUrl: String? = null
    var username: String? = null
    var id: String? = null
    var isFollowing: Boolean? = null
    var isPhoneAuth: Boolean? = null
    var isEmailAuth: Boolean? = null
    var isAppReview: Boolean? = null
    var isCancel: Boolean? = null
    var blocks: HashMap<String, Any>? = null
    var loginType: String? = null
    var timestamp: Long? = null
    var totalValue: Int? = null
    var sun: Int? = null
    var cloud: Int? = null
    var rain: Int? = null
    var selfIntro: String? = null
    var fcmToken: String? = null

    constructor()

    companion object {

        fun transformUser(dict: HashMap<String, Any>, key: String): UserModel {

            val user = UserModel()
            user.email = dict["email"] as? String
            user.isCancel = dict["isCancel"] as? Boolean
            user.profileImageUrl = dict["profileImageUrl"] as? String
            user.username = dict["username"] as? String
            user.blocks = dict["blocks"] as? HashMap<String, Any>
            user.isPhoneAuth = dict["isPhoneAuth"] as? Boolean
            user.isEmailAuth = dict["isEmailAuth"] as? Boolean
            user.isAppReview = dict["isAppReview"] as? Boolean
            user.fcmToken = dict["fcmToken"] as? String
            user.id = key
            val value = dict["value"] as? HashMap<String, Any>
            value?.let {

                user.sun = (it["sun"] as? Long)?.toInt()
                user.cloud = (it["cloud"] as? Long)?.toInt()
                user.rain = (it["rain"] as? Long)?.toInt()
                user.totalValue = user.sun!! + user.cloud!! + user.rain!!
            }
            user.selfIntro = dict[SELF_INTRODUCTION] as? String
            user.loginType = dict["loginType"] as? String
            user.timestamp = dict["timestamp"] as? Long

            return user
        }
    }
}