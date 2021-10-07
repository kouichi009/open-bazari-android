package jp.bazari.models

import jp.bazari.Apis.getCurrentUser
import jp.bazari.Apis.ratioHeightKey
import jp.bazari.Apis.ratioWidthKey
import java.io.Serializable

class Post : Serializable {

    var id: String? = null
    var title: String? = null
    var caption: String? = null
    var category1: String? = null
    var category2: String? = null
    var brand: String? = null
    var status: String? = null
    var shipPayer: String? = null
    var shipWay: String? = null
    var shipDeadLine: String? = null
    var shipFrom: String? = null
    var price: Int? = null
    var timestamp: Long? = null
    var uid: String? = null
    var sellerShouldDo: String? = null
    var transactionStatus: String? = null
    var thumbnailUrl: String? = null
    var thumbnailRatio: HashMap<String, Float>? = null
    var imageCount: Int? = null
    var imageUrls = ArrayList<String>()
    var ratios = ArrayList<HashMap<String, Float>>()
    var likeCount: Int? = null
    var likes: HashMap<String, Boolean>? = null
    var comments: HashMap<String, Any>? = null
    var isLiked: Boolean? = null
    var commentCount: Int? = null
    var shippedDateTimestamp: Long? = null
    var purchaserShouldDo: String? = null
    var purchaseDateTimestamp: Long? = null
    var purchaserUid: String? = null
    var commisionRate: Float? = null
    var isCancel: Boolean? = null


    constructor()

    companion object {

        fun transformPost(dict: HashMap<String, Any>, key: String): Post {

            val post = Post()
            post.id = key
            post.caption = dict["caption"] as String?
            post.isCancel = dict["isCancel"] as Boolean?
            post.uid = dict["uid"] as String?
            post.likes = dict["likes"] as HashMap<String, Boolean>?
            post.comments = dict["comments"] as HashMap<String, Any>?
            post.title = dict["title"] as String?
            post.brand = dict["brand"] as String?
            post.category1 = dict["category1"] as String?
            post.category2 = dict["category2"] as String?
            post.shipFrom = dict["shipFrom"] as String?
            post.shipPayer = dict["shipPayer"] as String?
            post.shipWay = dict["shipWay"] as String?
            post.status = dict["status"] as String?
            post.transactionStatus = dict["transactionStatus"] as String?
            post.purchaserShouldDo = dict["purchaserShouldDo"] as String?
            post.sellerShouldDo = dict["sellerShouldDo"] as String?
            post.purchaserUid = dict["purchaserUid"] as String?
            post.shipDeadLine = dict["shipDeadLine"] as String?
            post.thumbnailUrl = dict["thumbnail"] as String?
            post.timestamp = dict["timestamp"] as Long?
            post.shippedDateTimestamp = dict["shippedDateTimestamp"] as Long?
            post.purchaseDateTimestamp  = dict["purchaseDateTimestamp"] as Long?

            val likeCount = dict["likeCount"] as Long?
            likeCount?.let {
                post.likeCount = it.toInt()
            }
            val commentCount = dict["commentCount"] as Long?
            commentCount?.let {
                post.commentCount = it.toInt()
            }
            val imageCount = dict["imageCount"] as Long?
            imageCount?.let {
                post.imageCount = it.toInt()
            }
            val price = dict["price"] as Long?
            price?.let {
                post.price = it.toInt()
            }

            try {
                val commisionRate = dict["commisionRate"] as Double?
                commisionRate?.let {
                    post.commisionRate = it.toFloat()
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

            try {
                val commisionRate2 = dict["commisionRate"] as Long?
                commisionRate2?.let {
                    post.commisionRate = it.toFloat()
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

            try {

                val thumbnailRatio = dict["thumbnailRatio"] as HashMap<String, Double>?
                thumbnailRatio?.let {
                    var result = HashMap<String, Float>()
                    result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                    result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                    post.thumbnailRatio = result
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

            try {
                if (post.thumbnailRatio == null) {
                    val thumbnailRatio2 = dict["thumbnailRatio"] as HashMap<String, Long>?
                    thumbnailRatio2?.let {
                        var result = HashMap<String, Float>()
                        result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                        result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                        post.thumbnailRatio = result
                    }
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()

            }


            when (post.imageCount!! - 1) {
                0 -> {
                    val image0 = dict["image0"] as String?
                    var ratio0: HashMap<String, Float>? = null
                    try {
                        val ratio = dict["ratio0"] as HashMap<String, Double>?
                        ratio?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio0 = result
                        }
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }
                    try {
                        if (ratio0 == null) {
                            val ratio = dict["ratio0"] as HashMap<String, Long>?
                            ratio?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio0 = result
                            }
                        }
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    post.imageUrls.add(image0!!)
                    post.ratios.add(ratio0!!)
                }
                1 -> {
                    val image0 = dict["image0"] as? String
                    val image1 = dict["image1"] as? String
                    var ratio0: HashMap<String, Float>? = null
                    var ratio1: HashMap<String, Float>? = null
                    try {
                        val rat0 = dict["ratio0"] as? HashMap<String, Double>
                        val rat1 = dict["ratio1"] as? HashMap<String, Double>
                        rat0?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio0 = result
                        }

                        rat1?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio1 = result
                        }

                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    try {
                        if (ratio0 == null) {
                            val rat0 = dict["ratio0"] as? HashMap<String, Long>
                            rat0?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio0 = result
                            }
                        }

                        if (ratio1 == null) {
                            val rat1 = dict["ratio1"] as? HashMap<String, Long>
                            rat1?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio1 = result
                            }
                        }
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    post.imageUrls.add(image0!!)
                    post.imageUrls.add(image1!!)
                    post.ratios.add(ratio0!!)
                    post.ratios.add(ratio1!!)
                }

                2 -> {
                    val image0 = dict["image0"] as? String
                    val image1 = dict["image1"] as? String
                    val image2 = dict["image2"] as? String
                    var ratio0: HashMap<String, Float>? = null
                    var ratio1: HashMap<String, Float>? = null
                    var ratio2: HashMap<String, Float>? = null
                    try {
                        val rat0 = dict["ratio0"] as? HashMap<String, Double>
                        val rat1 = dict["ratio1"] as? HashMap<String, Double>
                        val rat2 = dict["ratio2"] as? HashMap<String, Double>
                        rat0?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio0 = result
                        }

                        rat1?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio1 = result
                        }

                        rat2?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio2 = result
                        }

                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    try {
                        if (ratio0 == null) {
                            val rat0 = dict["ratio0"] as? HashMap<String, Long>
                            rat0?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio0 = result
                            }
                        }

                        if (ratio1 == null) {
                            val rat1 = dict["ratio1"] as? HashMap<String, Long>
                            rat1?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio1 = result
                            }
                        }

                        if (ratio2 == null) {
                            val rat2 = dict["ratio2"] as? HashMap<String, Long>
                            rat2?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio2 = result
                            }
                        }
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    post.imageUrls.add(image0!!)
                    post.imageUrls.add(image1!!)
                    post.imageUrls.add(image2!!)
                    post.ratios.add(ratio0!!)
                    post.ratios.add(ratio1!!)
                    post.ratios.add(ratio2!!)
                }

                3 -> {
                    val image0 = dict["image0"] as? String
                    val image1 = dict["image1"] as? String
                    val image2 = dict["image2"] as? String
                    val image3 = dict["image3"] as? String
                    var ratio0: HashMap<String, Float>? = null
                    var ratio1: HashMap<String, Float>? = null
                    var ratio2: HashMap<String, Float>? = null
                    var ratio3: HashMap<String, Float>? = null
                    try {
                        val rat0 = dict["ratio0"] as? HashMap<String, Double>
                        val rat1 = dict["ratio1"] as? HashMap<String, Double>
                        val rat2 = dict["ratio2"] as? HashMap<String, Double>
                        val rat3 = dict["ratio3"] as? HashMap<String, Double>

                        rat0?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio0 = result
                        }

                        rat1?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio1 = result
                        }

                        rat2?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio2 = result
                        }

                        rat3?.let {
                            val result = HashMap<String, Float>()
                            result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                            result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                            ratio3 = result
                        }

                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    try {
                        if (ratio0 == null) {
                            val rat0 = dict["ratio0"] as? HashMap<String, Long>
                            rat0?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio0 = result
                            }                        }

                        if (ratio1 == null) {
                            val rat1 = dict["ratio1"] as? HashMap<String, Long>
                            rat1?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio1 = result
                            }
                        }

                        if (ratio2 == null) {
                            val rat2 = dict["ratio2"] as? HashMap<String, Long>
                            rat2?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio2 = result
                            }                        }

                        if (ratio3 == null) {
                            val rat3 = dict["ratio3"] as? HashMap<String, Long>
                            rat3?.let {
                                val result = HashMap<String, Float>()
                                result.put(ratioWidthKey, it[ratioWidthKey]!!.toFloat())
                                result.put(ratioHeightKey, it[ratioHeightKey]!!.toFloat())
                                ratio3 = result
                            }
                        }
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }

                    post.imageUrls.add(image0!!)
                    post.imageUrls.add(image1!!)
                    post.imageUrls.add(image2!!)
                    post.imageUrls.add(image3!!)

                    post.ratios.add(ratio0!!)
                    post.ratios.add(ratio1!!)
                    post.ratios.add(ratio2!!)
                    post.ratios.add(ratio3!!)

                }
            }

            getCurrentUser()?.uid?.let {
                if (post.likes != null) {
                    post.isLiked = post.likes!![it] != null
                }
            }

            return post
        }
    }
}