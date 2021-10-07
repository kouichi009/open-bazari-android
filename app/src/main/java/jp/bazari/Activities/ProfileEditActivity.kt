package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.bazari.Apis.*
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*
import java.io.File

class ProfileEditActivity : AppCompatActivity() {

    lateinit var currentUid : String

    var profileImageUrl: String? = null

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        backArrow.setOnClickListener {
            finish()
        }

        optionTv.text = "プロフィール編集"

        mSVProgressHUD = SVProgressHUD(this)


        currentUid = getCurrentUser()!!.uid

        saveBtn.isEnabled = false

        Api.User.observeUser(currentUid, {usermodel ->

            saveBtn.isEnabled = true

            usermodel?.let {
                Picasso.get()
                    .load(Uri.parse(it.profileImageUrl))
                    .into(profileIv)

                usernameEdit.setText(it.username)

                it.selfIntro?.let {selfIntroStr ->
                    self_introEdit.setText(selfIntroStr)
                }
            }
        })

        profileIv.setOnClickListener {
            pickImageFromGallery()
        }

        profileIvTv.setOnClickListener {
            pickImageFromGallery()
        }

        saveBtn.setOnClickListener {

            if (usernameEdit.text.toString().trim().equals("")){
                mSVProgressHUD?.showErrorWithStatus("ニックネームを空白にはできません。")
                return@setOnClickListener
            }

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)
            val result = HashMap<String, Any>()
            result.put("username", usernameEdit.text.toString())

            if (!self_introEdit.text.toString().trim().equals("")){
                result.put(getString(R.string.SELF_INTRODUCTION), self_introEdit.text.toString().trim())
            }
            //何も書いてなかったら。
            else {
                result.put(getString(R.string.SELF_INTRODUCTION), "")
            }
            profileImageUrl?.let {
                result.put("profileImageUrl", it)
            }

            Api.User.REF_USERS.child(currentUid).updateChildren(result, DatabaseReference.CompletionListener { databaseError, databaseReference ->

                mSVProgressHUD?.dismiss()

                if (databaseError != null) {

                    return@CompletionListener
                }

                mainActivity?.changeNavProfileIv()
                finish()
            })

        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            val originalImage = FileUtil.from(this, data?.data)

            compressImageForCorrectOrientation(originalImage, {file ->
                profileIv.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                HelperService.putProfileImageStorage(file, {profileImageUrl ->
                    //onSuccess
                    this.profileImageUrl = profileImageUrl
                }, {

                    //onError
                })
            })
        }
    }

    private fun compressImageForCorrectOrientation(originalImage: File, completion: (File) -> Unit) {

        Compressor(this)
            .compressToFileAsFlowable(originalImage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                completion(it)
            }
    }
}
