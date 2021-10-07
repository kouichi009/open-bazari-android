package jp.bazari.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import com.bigkoo.svprogresshud.SVProgressHUD
import com.ligl.android.widget.iosdialog.IOSDialog
import id.zelory.compressor.Compressor
import jp.bazari.Apis.*
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class PostActivity : AppCompatActivity() {

    private val TAG = "PostActivity"

    var mRatios =  ArrayList<HashMap<String, Float>>()

    var caption: String? = null
    var category: String? = null
    var brand: String? = null
    var status: String? = null
    var shipPayer: String? = null
    var shipWay: String? = null
    var shipDeadLine: String? = null
    var shipFrom: String? = null
    var timestamp: Long? = null
    var uid: String? = null
    var sellerShouldDo: String? = null
    var transactionStatus: String? = null

    var tupleImages: ArrayList<Pair<File, Int>> = ArrayList()
    var indexRow = 0

    //   val PICK_KEY = "PICK_KEY"

    var mSVProgressHUD: SVProgressHUD? = null

    var sharedPreferences: SharedPreferences? = null
    val SHIPPAYER = "shipPayer"
    val SHIPWAY = "shipWay"
    val SHIPDATE = "shipDate"
    val SHIPPREFECTURE = "shipPrefecture"

//    fun putSet() {
//
//        caption = "キャプション"
//        category = "フィルムカメラ"
//        brand = "Apple"
//        status = "新品・未使用"
//        shipPayer = "送料込み (あなたが負担)"
//        shipWay = "ヤマト宅急便"
//        shipDeadLine = "支払い後、4〜7日で発送"
//        shipFrom = "大阪府"
//        timestamp = getTimestamp()
//
//        getCurrentUser()?.let {
//            uid = it.uid
//        }
//        sellerShouldDo = "出品中"
//        transactionStatus = "sell"
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mSVProgressHUD = SVProgressHUD(this)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val forth = width / 4

        pickIv0.layoutParams.height = forth
        pickIv1.layoutParams.height = forth
        pickIv2.layoutParams.height = forth
        pickIv3.layoutParams.height = forth

        pickIv0.setImageResource(R.drawable.add)
        pickIv1.setImageResource(R.drawable.nonadd)
        pickIv2.setImageResource(R.drawable.nonadd)
        pickIv3.setImageResource(R.drawable.nonadd)

        sharedPreferences = getSharedPreferences("PostSharedPreferences", Context.MODE_PRIVATE)

        val shipP = sharedPreferences!!.getString(SHIPPAYER, shipPayerList[0])
        val shipD = sharedPreferences!!.getString(SHIPDATE, shipDatesList[2])
        val shipW = sharedPreferences!!.getString(SHIPWAY, "")
        val shipPrefe = sharedPreferences!!.getString(SHIPPREFECTURE, "")

        shipPayerTv.text = shipP
        shipPayer = shipP
        shipDeadLineTv.text = shipD
        shipDeadLine = shipD
        if (!shipW.equals("")) {
            shipWayTv.text = shipW
            shipWay = shipW
            shipWayTv.setTextColor(Color.parseColor("#000000"))
        }

        if (!shipPrefe.equals("")) {
            prefectureTv.text = shipPrefe
            shipFrom = shipPrefe
            prefectureTv.setTextColor(Color.parseColor("#000000"))
        }

        optionTv.text = "出品"
        backArrow.setOnClickListener {
            IOSDialog.Builder(this)
                .setMessage("前の画面に戻ると現在入力中のデータが消えますがよろしいですか?")
                .setPositiveButton(
                    "OK (戻る)"
                ) { dialog, which ->
                    finish()

                }
                .setNegativeButton(
                    "キャンセル", null
                ).show()
        }

        pickIv0.setOnClickListener {
            indexRow = 0
            pickImageFromGallery()
        }

        pickIv1.setOnClickListener {

            indexRow = 1
            if (indexRow > tupleImages.count()) {
                return@setOnClickListener
            }
            pickImageFromGallery()
        }

        pickIv2.setOnClickListener {
            indexRow = 2
            if (indexRow > tupleImages.count()) {
                return@setOnClickListener
            }
            pickImageFromGallery()
        }

        pickIv3.setOnClickListener {
            indexRow = 3
            if (indexRow > tupleImages.count()) {
                return@setOnClickListener
            }
            pickImageFromGallery()
        }

        postBtn.setOnClickListener {

            IOSDialog.Builder(this)
                .setMessage("投稿してよろしいですか？")
                .setPositiveButton(
                    "投稿する"
                ) { dialog, which ->

                    postIv()

                }
                .setNegativeButton(
                    "キャンセル", null
                ).show()
        }

        captionLayout.setOnClickListener {

            val requestCode = CAPTION_CODE
            val intent = Intent(this, CaptionEditActivity::class.java).apply {
                putExtra(IntentKey.CAPTION.name, captionTv.text.toString())
            }
            startActivityForResult(intent, requestCode)
        }

        categoryLayout.setOnClickListener {
            val requestCode = CATEGORY_CODE
            val intent = Intent(this, CategorySelectActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        brandLayout.setOnClickListener {
            val requestCode = BRAND_CODE
            val intent = Intent(this, BrandSelectActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        productStatusLayout.setOnClickListener {
            val requestCode = PRODUCT_STATUS_CODE
            val intent = Intent(this, ProductStatusActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        shipPayerLayout.setOnClickListener {
            val requestCode = SHIPPAYER_CODE
            val intent = Intent(this, ShipPayerSelectActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        shipWayLayout.setOnClickListener {
            val requestCode = SHIPWAY_CODE
            val intent = Intent(this, ShipInfoSelectActivity::class.java).apply {
                putExtra(IntentKey.SHIPPAYERSELECTED.name, shipPayer)
                putExtra(IntentKey.SHIPINFOSELECTION.name, getString(R.string.shipway))
            }
            startActivityForResult(intent, requestCode)
        }

        shipDeadLineLayout.setOnClickListener {
            val requestCode = SHIPDEADLINE_CODE
            val intent = Intent(this, ShipInfoSelectActivity::class.java).apply {
                putExtra(IntentKey.SHIPINFOSELECTION.name, getString(R.string.shipdeadline))
            }
            startActivityForResult(intent, requestCode)
        }

        shipPrefecutreLayout.setOnClickListener {
            val requestCode = PREFECTURE_CODE
            val intent = Intent(this, ShipInfoSelectActivity::class.java).apply {
                putExtra(IntentKey.SHIPINFOSELECTION.name, getString(R.string.prefecture))
            }
            startActivityForResult(intent, requestCode)
        }

        priceEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(text: Editable) {

                if (text.count() < 1) return;
                else if (text.toString().substring(0, 1).equals("0")) {
                    priceEdit.setText("")
                    commisionTv.text = ""
                    profitTv.text = ""
                    return
                }
                afterTex(text.toString().toInt())
            }
        })


        closeBtn0.setOnClickListener {

            pushCloseBtn(0)
        }

        closeBtn1.setOnClickListener {
            pushCloseBtn(1)

        }

        closeBtn2.setOnClickListener {
            pushCloseBtn(2)

        }

        closeBtn3.setOnClickListener {
            pushCloseBtn(3)

        }

        showHideCloseBtn()

    }

    fun pushCloseBtn(index: Int) {

        val cta = tupleImages.count()
        tupleImages.removeAt(index)
        val ctb = tupleImages.count()

        //削除した画像より後ろのタプルのソートを一つ繰り下げる
        var count = 0
        tupleImages.forEach { tupleImage ->
            if (index < tupleImage.second) {
                var originalImage: File? = null
                tupleImages[count] = Pair(tupleImage.first, tupleImage.second - 1)
            }
            count++
        }
        mRatios.removeAt(index)
        showHideCloseBtn()

        when (tupleImages.count()) {

            0 -> {
                pickIv0.setImageResource(R.drawable.add)
                pickIv1.setImageResource(R.drawable.nonadd)
                pickIv2.setImageResource(R.drawable.nonadd)
                pickIv3.setImageResource(R.drawable.nonadd)
            }

            1 -> {
                pickIv0.setImageBitmap(BitmapFactory.decodeFile(tupleImages[0].first.absolutePath))
                pickIv1.setImageResource(R.drawable.add)
                pickIv2.setImageResource(R.drawable.nonadd)
                pickIv3.setImageResource(R.drawable.nonadd)
            }

            2 -> {
                pickIv0.setImageBitmap(BitmapFactory.decodeFile(tupleImages[0].first.absolutePath))
                pickIv1.setImageBitmap(BitmapFactory.decodeFile(tupleImages[1].first.absolutePath))
                pickIv2.setImageResource(R.drawable.add)
                pickIv3.setImageResource(R.drawable.nonadd)

            }

            3 -> {
                pickIv0.setImageBitmap(BitmapFactory.decodeFile(tupleImages[0].first.absolutePath))
                pickIv1.setImageBitmap(BitmapFactory.decodeFile(tupleImages[1].first.absolutePath))
                pickIv2.setImageBitmap(BitmapFactory.decodeFile(tupleImages[2].first.absolutePath))
                pickIv3.setImageResource(R.drawable.add)
            }
        }
    }

    fun showHideCloseBtn() {

        when (tupleImages.count()) {
            0 -> {
                closeBtn0.visibility = View.INVISIBLE
                closeBtn1.visibility = View.INVISIBLE
                closeBtn2.visibility = View.INVISIBLE
                closeBtn3.visibility = View.INVISIBLE
            }

            1 -> {
                closeBtn0.visibility = View.VISIBLE
                closeBtn1.visibility = View.INVISIBLE
                closeBtn2.visibility = View.INVISIBLE
                closeBtn3.visibility = View.INVISIBLE
            }

            2 -> {
                closeBtn0.visibility = View.VISIBLE
                closeBtn1.visibility = View.VISIBLE
                closeBtn2.visibility = View.INVISIBLE
                closeBtn3.visibility = View.INVISIBLE
            }

            3 -> {
                closeBtn0.visibility = View.VISIBLE
                closeBtn1.visibility = View.VISIBLE
                closeBtn2.visibility = View.VISIBLE
                closeBtn3.visibility = View.INVISIBLE
            }

            4 -> {
                closeBtn0.visibility = View.VISIBLE
                closeBtn1.visibility = View.VISIBLE
                closeBtn2.visibility = View.VISIBLE
                closeBtn3.visibility = View.VISIBLE
            }
        }

    }

    private fun afterTex(price: Int) {

        if (price < minimumPrice) {
            commisionTv.text = ""
            profitTv.text = ""
            return
        }

        val commision = price.toDouble() * commisionRate
        commisionTv.text = getFormatPrice(commision.toInt()) + "円"

        profitTv.text = getFormatPrice(price - commision.toInt()) + "円"
    }


    private fun pickImageFromGallery() {

        requestAlbumGalleryPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 返却結果ステータスとの比較
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAPTION_CODE -> {
                    val bundle = data?.extras
                    caption = bundle?.getString(IntentKey.CAPTION.name)
                    if (caption.equals("")) {
                        caption = null
                    }
                    captionTv.text = caption
                }

                CATEGORY_CODE -> {
                    val bundle = data?.extras
                    category = bundle?.getString(IntentKey.CATEGORY.name)
                    categoryTv.text = category
                    categoryTv.setTextColor(Color.parseColor("#000000"))
                }

                BRAND_CODE -> {
                    val bundle = data?.extras
                    brand = bundle?.getString(IntentKey.BRAND.name)

                    if (brand!!.equals("メーカー名なし")) {
                        brandTv.text = "(任意)"
                        brand = null
                        brandTv.setTextColor(Color.parseColor("#bfbfbf"))

                    } else {
                        brandTv.text = brand
                        brandTv.setTextColor(Color.parseColor("#000000"))
                    }
                }

                PRODUCT_STATUS_CODE -> {
                    val bundle = data?.extras
                    status = bundle?.getString(IntentKey.PRODUCT_STATUS.name)
                    statusTv.text = status
                    statusTv.setTextColor(Color.parseColor("#000000"))
                }

                SHIPPAYER_CODE -> {
                    val bundle = data?.extras
                    val sp = bundle?.getString(IntentKey.SHIPPAYER.name)
                    shipPayerTv.text = sp
                    shipPayerTv.setTextColor(Color.parseColor("#000000"))

                    if (!shipPayer.equals(sp)) {
                        shipWay = null
                        shipWayTv.text = "(任意)"
                        shipWayTv.setTextColor(Color.parseColor("#bfbfbf"))
                    }

                    shipPayer = sp

                }

                SHIPWAY_CODE -> {
                    val bundle = data?.extras
                    val shipInfo = bundle?.getString(IntentKey.SHIPINFO.name)
                    shipWay = shipInfo
                    shipWayTv.text = shipInfo
                    shipWayTv.setTextColor(Color.parseColor("#000000"))
                }

                SHIPDEADLINE_CODE -> {
                    val bundle = data?.extras
                    val shipInfo = bundle?.getString(IntentKey.SHIPINFO.name)
                    shipDeadLine = shipInfo
                    shipDeadLineTv.text = shipInfo
                    shipDeadLineTv.setTextColor(Color.parseColor("#000000"))
                }

                PREFECTURE_CODE -> {
                    val bundle = data?.extras
                    val shipInfo = bundle?.getString(IntentKey.SHIPINFO.name)
                    shipFrom = shipInfo
                    prefectureTv.text = shipInfo
                    prefectureTv.setTextColor(Color.parseColor("#000000"))
                }

                //ava.lang.IllegalStateException: bitmap must not be nullが出たら、下のYoutube解説を見る。
                //https://www.youtube.com/watch?v=bEXrO6glWPk&feature=youtu.be
                IMAGE_PICK_CODE -> {

                    lateinit var originalImage: File

                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    val cursor = this.getContentResolver().query(data?.data, projection, null, null, null)
                    if (cursor != null) {
                        var path: String? = null
                        if (cursor!!.moveToFirst()) {
                            path = cursor!!.getString(0)
                        }
                        cursor!!.close()
                        if (path != null) {
                            val file = File(path)
                            try {
                                //圧縮
                                originalImage = Compressor(this).compressToFile(file)
                            } catch (e: Exception) {
                                //lateinit var なのでなにか入れる必要がある。
                                originalImage = file
                                e.printStackTrace()
                                return
                            }

                        }
                    }

                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(originalImage.absolutePath, options)
                    val height = (options.outHeight).toFloat()
                    val width = (options.outWidth).toFloat()

                    val ratioWidth = height / width
                    val ratioHeight = width / height

                    var ratioDict: HashMap<String, Float> = HashMap()
                    ratioDict.put(ratioWidthKey, ratioWidth)
                    ratioDict.put(ratioHeightKey, ratioHeight)

                    if (tupleImages.canAccess(indexRow!!)) {
                        tupleImages[indexRow!!] = Pair(originalImage, indexRow!!)
                        mRatios[indexRow!!] = ratioDict
                    } else {
                        tupleImages.add(Pair(originalImage, tupleImages.count()))
                        mRatios.add(ratioDict)

                    }
                    showHideCloseBtn()
                    updateImges(originalImage)

                }
            }
        }
    }


    private fun postIv() {

        var title: String = ""
        var inputPrice: Int = 0
        title = titleEdit.text.toString()
        val priceStr = priceEdit.text.toString()
        if (!priceStr.equals("")) {
            inputPrice = priceStr.toInt()
        }

        if (shipWay == null) {
            shipWay = "未定"
        }

        if (tupleImages.count() == 0) {
            mSVProgressHUD?.showErrorWithStatus("商品画像は１枚以上選択してください。")
            return
        } else if (title.equals("")) {
            mSVProgressHUD?.showErrorWithStatus("商品名を入力してください。")
            return
        } else if (caption == null) {
            mSVProgressHUD?.showErrorWithStatus("商品の説明を入力してください。")
            return
        } else if (category == null) {
            mSVProgressHUD?.showErrorWithStatus("カテゴリを選択してください。")
            return
        } else if (status == null) {
            mSVProgressHUD?.showErrorWithStatus("商品の状態を選択してください。")
            return
        } else if (shipFrom == null) {
            mSVProgressHUD?.showErrorWithStatus("発送元の地域を選択してください。")
            return
        } else if (inputPrice == 0) {
            mSVProgressHUD?.showErrorWithStatus("商品価格を入力してください。")
            return
        }

        if (inputPrice < minimumPrice) {
            mSVProgressHUD?.showErrorWithStatus("商品価格は、"+ minimumPrice+"円以上で設定してください。")
            return

        } else if (inputPrice > maximumPrice) {
            mSVProgressHUD?.showErrorWithStatus("商品価格の上限は" + getFormatPrice(maximumPrice) + "円です。")
            return
        }

        val editor = sharedPreferences!!.edit()
        editor.putString(SHIPPAYER, shipPayer)
        if (shipWay.equals("未定")) {
            editor.remove(SHIPWAY)
        } else {
            editor.putString(SHIPWAY, shipWay)
        }
        editor.putString(SHIPDATE, shipDeadLine)
        editor.putString(SHIPPREFECTURE, shipFrom)
        editor.apply()

        mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)


        HelperService.uploadPostDataToServer(this@PostActivity,
            title,
            caption,
            getString(R.string.CAMERA),
            category,
            brand,
            status,
            shipPayer,
            shipWay,
            shipDeadLine,
            shipFrom,
            inputPrice,
            tupleImages,
            mRatios,
            tupleImages.count(),
            {
                //OnSuccess
                mSVProgressHUD?.dismiss()
                makeToast(this@PostActivity, "投稿しました。")
                finish()

            },
            {
                //OnError
                mSVProgressHUD?.dismiss()
                IOSDialog.Builder(this)
                    .setTitle("エラー")
                    .setMessage("投稿できませんでした。")
                    .setPositiveButton("OK", null).show()
            })
    }

    fun updateImges(file: File) {

        when (tupleImages.count()) {

            0 -> {
                pickIv0.setImageResource(R.drawable.add)
                pickIv1.setImageResource(R.drawable.nonadd)
                pickIv2.setImageResource(R.drawable.nonadd)
                pickIv3.setImageResource(R.drawable.nonadd)
            }

            1 -> {
                pickIv1.setImageResource(R.drawable.add)
                pickIv2.setImageResource(R.drawable.nonadd)
                pickIv3.setImageResource(R.drawable.nonadd)
            }

            2 -> {
                pickIv2.setImageResource(R.drawable.add)
                pickIv3.setImageResource(R.drawable.nonadd)

            }

            3 -> {
                pickIv3.setImageResource(R.drawable.add)
            }
        }


        when (indexRow) {

            0 -> {
                pickIv0.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            }

            1 -> {
                pickIv1.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            }

            2 -> {
                pickIv2.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))

            }

            3 -> {
                pickIv3.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            }
        }


    }

    private fun requestAlbumGalleryPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("画像を利用するには、バザリからのアクセスを許可して下さい。")
                .setPositiveButton("許可する", DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this@PostActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ALBUM_PERMISSION_CODE
                    )
                })
                .setNegativeButton("今はしない", DialogInterface.OnClickListener { dialog, which ->

                    makeToast(this, "画像の利用が許可されませんでした。")
                    dialog.dismiss()
                })
                .create().show()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ALBUM_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == ALBUM_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Album Permission GRANTED", Toast.LENGTH_SHORT).show()

                //Intent to pick image
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_PICK_CODE)

            } else {
                makeToast(this, "画像の利用が許可されませんでした。")
            }
        }
    }

}

//ArrayListの拡張関数
fun ArrayList<Pair<File, Int>>.canAccess(index: Int): Boolean {
    //thisはArrayListクラスをさす。現在選択してる画像数(this.count)
    if (this.count() - 1 >= index)
        return true
    else
        return false
}