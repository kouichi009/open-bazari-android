package jp.bazari.Apis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import jp.bazari.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object HelperService {


    fun uploadPostDataToServer(
        context: Context,
        title: String,
        caption: String?,
        category1: String?,
        category2: String?,
        brand: String?,
        status: String?,
        shipPayer: String?,
        shipWay: String?,
        shipDeadLine: String?,
        shipFrom: String?,
        price: Int?,
        tupleImages: ArrayList<Pair<File, Int>>,
        ratios: ArrayList<HashMap<String, Float>>,
        imageCount: Int,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {

        var thumbnailUrl: String? = null
        var thumbnailRatio: HashMap<String, Float>? = null
        var imageUrls = ArrayList<String>()

        var imageUrl_0: String? = null
        var imageUrl_1: String? = null
        var imageUrl_2: String? = null
        var imageUrl_3: String? = null

        val result = makeThumbnail(tupleImages[0].first, context)
        val uri = result["uri"] as Uri
        thumbnailRatio = result[context.getString(R.string.thumbnailRatio)] as HashMap<String, Float>

        putThumbnailStorage(uri, {
            //OnSuccess
            thumbnailUrl = it

            setDatabaseToPosts(
                thumbnailUrl,
                thumbnailRatio,
                imageUrls,
                ratios,
                imageCount,
                title,
                caption!!,
                category1!!,
                category2!!,
                status!!,
                shipPayer!!,
                shipWay!!,
                shipDeadLine!!,
                shipFrom!!,
                price!!,
                brand,
                onSuccess,
                onError
            )

        }, {
            //OnError
            onError()
        })

        var count = 0
        for (tupleImage in tupleImages) {

            putImageStorage(tupleImage, { tupleImageUrl ->

                //onSuccess
                //sort: Int
                when (tupleImageUrl.second) {
                    0 -> {
                        //imageUrl: String
                        imageUrl_0 = tupleImageUrl.first
                    }

                    1 -> {
                        //imageUrl: String
                        imageUrl_1 = tupleImageUrl.first
                    }

                    2 -> {
                        //imageUrl: String
                        imageUrl_2 = tupleImageUrl.first
                    }

                    3 -> {
                        //imageUrl: String
                        imageUrl_3 = tupleImageUrl.first
                    }
                }

                count++

                if (count == tupleImages.count()) {

                    when (tupleImages.count() - 1) {

                        0 -> {
                            imageUrls.add(imageUrl_0!!)
                        }

                        1 -> {
                            imageUrls.add(imageUrl_0!!)
                            imageUrls.add(imageUrl_1!!)
                        }

                        2 -> {
                            imageUrls.add(imageUrl_0!!)
                            imageUrls.add(imageUrl_1!!)
                            imageUrls.add(imageUrl_2!!)
                        }

                        3 -> {
                            imageUrls.add(imageUrl_0!!)
                            imageUrls.add(imageUrl_1!!)
                            imageUrls.add(imageUrl_2!!)
                            imageUrls.add(imageUrl_3!!)
                        }

                    }
                }

                if (imageUrls.count() == tupleImages.count()) {
                    setDatabaseToPosts(
                        thumbnailUrl,
                        thumbnailRatio,
                        imageUrls,
                        ratios,
                        imageCount,
                        title,
                        caption!!,
                        category1!!,
                        category2!!,
                        status!!,
                        shipPayer!!,
                        shipWay!!,
                        shipDeadLine!!,
                        shipFrom!!,
                        price!!,
                        brand,
                        onSuccess,
                        onError
                    )
                }

            }, {
                //onError
                onError()
            })
        }
    }

    fun putThumbnailStorage(thumbnailUrl: Uri?, onSuccess: (String) -> Unit, onError: () -> Unit) {
        val autoKey = UUID.randomUUID().toString()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val storageRef = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("posts").child(autoKey)
        storageRef.putFile(thumbnailUrl!!).continueWithTask { task ->
            if (!task.isSuccessful) {
                onError()
            }
            return@continueWithTask storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError()
                return@addOnCompleteListener
            }

            onSuccess(task.result.toString())
        }
    }


    fun putImageStorage(tupleImage: Pair<File, Int>, onSuccess: (Pair<String, Int>) -> Unit, onError: () -> Unit) {

        val autoKey = UUID.randomUUID().toString()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val storageRef = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("posts").child(autoKey)

        val uri = Uri.fromFile(tupleImage.first)
        storageRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                onError()
            }
            return@continueWithTask storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError()
                return@addOnCompleteListener
            }
            onSuccess(Pair(task.result.toString(), tupleImage.second))
        }
    }


    fun setDatabaseToPosts(
        thumbnailUrl: String?,
        thumbnailRatio: HashMap<String, Float>?,
        imageUrls: ArrayList<String>,
        ratios: ArrayList<HashMap<String, Float>>,
        imageCount: Int,
        title: String,
        caption: String,
        category1: String,
        category2: String,
        status: String,
        shipPayer: String,
        shipWay: String,
        shipDate: String,
        shipFrom: String,
        price: Int,
        brand: String?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {

        if (thumbnailUrl != null && imageUrls.count() == imageCount) {

            if (getCurrentUser() == null) {
                onError()
                return
            }

            val currentUid = getCurrentUser()!!.uid
            val timestamp = getTimestamp()

            val ref = FirebaseDatabase.getInstance().reference

            var result = HashMap<String, Any?>()
            result.put("thumbnail", thumbnailUrl)
            result.put("thumbnailRatio", thumbnailRatio)
            result.put("uid", currentUid)
            result.put("title", title)
            result.put("caption", caption)
            result.put("timestamp", timestamp)
            result.put("imageCount", imageCount)
            result.put("category1", category1)
            result.put("category2", category2)
            result.put("status", status)
            result.put("shipPayer", shipPayer)
            result.put("shipWay", shipWay)
            result.put("shipDeadLine", shipDate)
            result.put("shipFrom", shipFrom)
            result.put("price", price)
            result.put("brand", brand)
            result.put("transactionStatus", TransactionStatus.sell.toString())
            result.put("sellerShouldDo", nowOnSell)

            when (imageCount - 1) {
                0 -> {
                    result.put("image0", imageUrls[0])
                    result.put("ratio0", ratios[0])
                }

                1 -> {
                    result.put("image0", imageUrls[0])
                    result.put("image1", imageUrls[1])
                    result.put("ratio0", ratios[0])
                    result.put("ratio1", ratios[1])
                }

                2 -> {
                    result.put("image0", imageUrls[0])
                    result.put("image1", imageUrls[1])
                    result.put("image2", imageUrls[2])
                    result.put("ratio0", ratios[0])
                    result.put("ratio1", ratios[1])
                    result.put("ratio2", ratios[2])
                }

                3 -> {
                    result.put("image0", imageUrls[0])
                    result.put("image1", imageUrls[1])
                    result.put("image2", imageUrls[2])
                    result.put("image3", imageUrls[3])
                    result.put("ratio0", ratios[0])
                    result.put("ratio1", ratios[1])
                    result.put("ratio2", ratios[2])
                    result.put("ratio3", ratios[3])
                }


            }

            val key = ref.child(DBKey.posts.toString()).push().key
            ref.child(DBKey.posts.toString()).child(key!!)
                .setValue(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        onError()
                        return@CompletionListener
                    }
                    ref.child(DBKey.myPosts.toString()).child(currentUid!!).child(key!!).child("timestamp")
                        .setValue(timestamp)
                    Api.MySellPosts.REF_MYSELL.child(getCurrentUser()!!.uid).child(key!!).child("timestamp")
                        .setValue(timestamp)
                    onSuccess()
                })

        }

    }



    fun makeThumbnail(originalImage: File, context: Context): HashMap<String, Any> {

        val origiPath = originalImage.path
        val bm = BitmapFactory.decodeFile(origiPath)
        lateinit var bitmap: Bitmap

        if (bm.height > 1600) {
            bitmap = Bitmap.createScaledBitmap(bm, bm.width / 3, bm.height / 3, true)
        } else if (bm.height > 800) {
            bitmap = Bitmap.createScaledBitmap(bm, bm.width / 2, bm.height / 2, true)
        } else {
            bitmap = bm
        }

        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()

        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null)

        val thumbnailWidthRatio = height / width
        val thumbnailHeightRatio = width / height
        val thumbnailUri = Uri.parse(path)

        var result = HashMap<String, Any>()
        result.put("uri", thumbnailUri)

        var result2 = HashMap<String, Float>()
        result2.put(ratioWidthKey, thumbnailWidthRatio)
        result2.put(ratioHeightKey, thumbnailHeightRatio)
        result.put(context.getString(R.string.thumbnailRatio), result2)

        return result
    }

    fun putProfileImageStorage(profileImage: File, onSuccess: (String) -> Unit, onError: () -> Unit) {

        val autoKey = UUID.randomUUID().toString()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val storageRef = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("posts").child(autoKey)

        val uri = Uri.fromFile(profileImage)
        storageRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                onError()
            }
            return@continueWithTask storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError()
                return@addOnCompleteListener
            }
            onSuccess(task.result.toString())
        }
    }
}