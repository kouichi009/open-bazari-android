package jp.bazari.Apis

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jp.bazari.Activities.RegisterActivity
import java.text.SimpleDateFormat
import java.util.*


fun goToRegsiterActivity(mContext: Context) {
    val intent = Intent(mContext, RegisterActivity::class.java)
    mContext.startActivity(intent)
}

fun makeToast(content: Context, message: String) {
    Toast.makeText(content, message, Toast.LENGTH_SHORT).show()
}

fun getTimestamp(): Long {
    return (System.currentTimeMillis()/ 1000L)
}

fun getFormatPrice(price: Int): String {
    return String.format("%,d", price)
}

fun getHyphenDateFromTimestamp(timestamp: Long): String {

    val ts = timestamp * 1000L
    val sd = SimpleDateFormat("yyyy-MM-dd")
    val date = Date(ts)
    val result = sd.format(date)

    return result
}

fun getDateFromHyphen(string: String): String {

    val wordList = string.split("-")
    val count4 = wordList.count()

    var count = 0
    var year = ""
    var month = ""
    var day = ""
    for(word in wordList) {
        if (count == 0) {
            year = word + "年"
        }

        else if (count == 1) {
            month = word + "月"
        }

        else if (count == 2) {
            day = word + "日"
        }
        count++
    }

    val dateStr = year + month +day

    return dateStr
}

fun getDateFromTimestamp(timestamp: Long): String {

    val ts = timestamp * 1000L
    val sd = SimpleDateFormat("yyyy-MM-dd")
    val date = Date(ts)
    val result = sd.format(date)

    val wordList = result.split("-")
    val count4 = wordList.count()

    var count = 0
    var year = ""
    var month = ""
    var day = ""
    for(word in wordList) {
        if (count == 0) {
            year = word + "年"
        }

        else if (count == 1) {
            month = word + "月"
        }

        else if (count == 2) {
            day = word + "日"
        }
        count++
    }

    val dateStr = year + month +day

    return dateStr
}


fun getCurrentUser(): FirebaseUser? {

    FirebaseAuth.getInstance().currentUser?.let {
        return it
    }

    return null

}
