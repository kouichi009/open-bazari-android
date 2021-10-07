package jp.bazari.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.eggheadgames.siren.Siren
import com.eggheadgames.siren.SirenVersionCheckType
import com.squareup.picasso.Picasso
import jp.bazari.Apis.*
import jp.bazari.Fragments.HomeFragment
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

var mainActivity: MainActivity? = null

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ProfileEditActivityInterface {

    override fun changeNavProfileIv() {
        val navHeader = nav_view.getHeaderView(0)
        myUser(navHeader)
    }

    var currentUid: String? = null

    private val TAG = "MainActivity"


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mainActivity = this


    //    checkSiren()


        printKeyHashForFacebookLogin()


        fab.setOnClickListener { view ->

            getCurrentUser()?.let {
                val intent = Intent(this, PostActivity::class.java)
                startActivity(intent)

            }?: run {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }

        getCurrentUser()?.let {
            currentUid = it.uid

        } ?: run {
            changeNavItemsText()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()





        nav_view.setNavigationItemSelectedListener(this)

        supportActionBar!!.setTitle(R.string.fragment_home)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_home_menu)
        toolbar.setTitleTextColor(Color.BLACK)

        displayScreen(-1)

        val navHeader = nav_view.getHeaderView(0)

        myUser(navHeader)

        navHeader.profileIv.setOnClickListener {

            val intent = Intent(this@MainActivity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, currentUid)
            }
            startActivity(intent)
        }
        navHeader.usernameTv.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileUserActivity::class.java).apply {
                putExtra(IntentKey.USERID.name, currentUid)
            }
            startActivity(intent)
        }
    }

    fun myUser(navHeader: View) {
        getCurrentUser()?.let {
            Api.User.observeUser(it.uid, {

                it?.let {
                    Picasso.get()
                        .load(Uri.parse(it.profileImageUrl))
                        .into(navHeader.profileIv)

                    navHeader.usernameTv.text = it.username
                }
            })
        } ?: run {
            navHeader.usernameTv.visibility = View.GONE
            navHeader.profileIv.visibility = View.GONE
        }
    }

    private fun changeNavItemsText() {
        val menu = nav_view.menu
        val nav_home = menu.findItem(R.id.nav_home)
        nav_home.setTitle("新規登録/ログイン")


        //NavigationDrawerのアイテムをグレーにする
        for (i in 0 until menu.size()) {

            if (i == 0 || i == 9) {
                continue
            }

            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            spanString.setSpan(ForegroundColorSpan(Color.LTGRAY), 0, spanString.length, 0) //fix the color to white
            item.title = spanString
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        displayScreen(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun displayScreen(id: Int) {

        var fragment: Fragment? = null
        var intent: Intent? = null
        when (id) {
            R.id.nav_home -> {

                getCurrentUser()?.let {
                    fragment = HomeFragment()
                } ?: run {
                    intent = Intent(this@MainActivity, RegisterActivity::class.java)
                }
            }
            R.id.nav_notification -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, NotificationActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }
            }
            R.id.nav_post -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, PostActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }
            }
            R.id.nav_sell -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, SellListActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }
            }
            R.id.nav_purchase -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, PurchaseListActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }
            }
            R.id.nav_good -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, LikeListActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }
            }
            R.id.nav_comment -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, CommentListActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }

            }
            R.id.nav_sell_management -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, SalesManagementActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }

            }
            R.id.nav_setting -> {
                getCurrentUser()?.let {
                    intent = Intent(this@MainActivity, SettingActivity::class.java)
                } ?: run {
                    makeToast(this, "会員登録してください。")
                }

            }

            R.id.nav_question -> {

                intent = Intent(this@MainActivity, QuestionActivity::class.java)
            }
            else -> {
                fragment = HomeFragment()
            }
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.relLayout, it)
                .commit()
            return

        }

        intent?.let {
            startActivity(it)
        }
    }


    private fun printKeyHashForFacebookLogin() {

        try {
            val info = packageManager.getPackageInfo("jp.bazari", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {

                val messageDigest = MessageDigest.getInstance("SHA")
                messageDigest.update(signature.toByteArray())
                Log.d("KEYHASH", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT))
                print("")
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }

    //force user to update app
    fun checkSiren() {


        val siren = Siren.getInstance(getApplicationContext());
        siren.checkVersion(this, SirenVersionCheckType.IMMEDIATELY, SIREN_JSON_URL);
    }
}
