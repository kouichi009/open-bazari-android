package jp.bazari.Activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.View
import android.widget.AdapterView
import com.bigkoo.svprogresshud.SVProgressHUD
import com.squareup.picasso.Picasso
import jp.bazari.Adapters.RecyclerViewAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Apis.makeToast
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.activity_profile_user.*
import kotlinx.android.synthetic.main.selfintro_info.*
import kotlinx.android.synthetic.main.snippet_profile_info.*
import kotlinx.android.synthetic.main.snippet_top_more_toolbar.*
import java.util.*

class ProfileUserActivity : AppCompatActivity() {

    private val TAG = "ProfileUserActivity"

    lateinit var mContext: Context
    var posts: ArrayList<Post> = ArrayList()

    lateinit var userId: String

    var currentUid: String? = null

    var mSVProgressHUD: SVProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        top_layout.requestFocus()

        val bundle = intent.extras
        userId = bundle.getString(IntentKey.USERID.name)
        backArrow.setOnClickListener {
            finish()
        }

        mSVProgressHUD = SVProgressHUD(this)

        optionTv.text = "プロフィール"

        getCurrentUser()?.let {
            currentUid = it.uid
        }

        getPostList {

            if (it) {
                setAdapter()
            }
        }

//        valueLayout.setOnClickListener {
//            val intent = Intent(this, ValueActivity::class.java)
//            startActivity(intent)
//        }

        if (currentUid == userId) {

            moreBtn.setOnClickListener {


                val popupMenu = PopupMenu(this, moreBtn)
                popupMenu.inflate(R.menu.profile_menu)
                popupMenu.setOnMenuItemClickListener { item ->

                    when (item.itemId) {
                        R.id.menu_item_profile -> {
                            val intent = Intent(this, ProfileEditActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    false
                }
                popupMenu.show()
            }

        } else {
            moreBtn.visibility = View.INVISIBLE

        }



    }

    override fun onResume() {
        super.onResume()

        Api.User.observeUser(userId, { usermodel ->

            usermodel?.let {

                //削除済みユーザーであれば、
                if (it.isCancel != null) {
                    makeToast(this, "削除済みのユーザーです。")
                    finish()
                }

                Picasso.get()
                    .load(Uri.parse(it.profileImageUrl))
                    .into(profileIv)

                usernameTv.text = it.username

                if (it.selfIntro == null) {
                    self_introTv.text = ""
                }

                it.selfIntro?.let {
                    self_introTv.text = it
                }

                sunTv.text = it.sun.toString()
                cloudTv.text= it.cloud.toString()
                rainTv.text =it.rain.toString()

            }
        })
    }
    fun setAdapter() {
        postCountTv.text = posts.count().toString()


        recyclerView.isNestedScrollingEnabled = false
        val recyclerViewAdapter = RecyclerViewAdapter(posts, this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = recyclerViewAdapter

        val onItemClick = AdapterView.OnItemClickListener { parent, view, position, id ->

            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(IntentKey.POST_ID.name, posts.get(position).id!!)
            }

            startActivity(intent)
        }

     //   gridView.setOnItemClickListener(onItemClick)
    }

    fun getPostList(completed: (Boolean) -> Unit) {

        var count = 0
        posts.clear()

        mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Black)
        Api.MyPosts.observeMyPosts(userId, { post, postCount ->

            if (postCount == 0) {
                mSVProgressHUD?.dismiss()
                return@observeMyPosts
            }

            count++

            post?.let {
                posts.add(0, post)
            }

            if (count == postCount) {
                mSVProgressHUD?.dismiss()
                completed(true)
            }
        })

    }
}
