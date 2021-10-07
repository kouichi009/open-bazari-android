package jp.bazari.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import jp.bazari.Adapters.ViewPagerAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.NotificationActivityInterface
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Fragments.NotificationFragment
import jp.bazari.R
import jp.bazari.models.Notification
import jp.bazari.models.Post
import jp.bazari.models.UserModel
import kotlinx.android.synthetic.main.snippet_sell_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

var notificationActivity: NotificationActivity? = null

class NotificationActivity : AppCompatActivity(), NotificationActivityInterface {

    override fun updateNotificationAC() {
        setDatas()
    }


    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null

    //Fragments
    lateinit var notificationFragment0: NotificationFragment
    lateinit var notificationFragment1: NotificationFragment

    var forYouNotificationList: ArrayList<Notification> = ArrayList()
    var transactionNotificationList: ArrayList<Notification> = ArrayList()

    var forYouPostList: ArrayList<Post> = ArrayList()
    var transactionPostList: ArrayList<Post> = ArrayList()

    var forYouUserList: ArrayList<UserModel> = ArrayList()
    var transactionUserList: ArrayList<UserModel> = ArrayList()

    var tabTitle = arrayOf("あなた宛", "取引")

    var badgeCount_you = 0
    var badgeCount_Transaction = 0

    var currentUid: String? = null

    var usermodels = ArrayList<UserModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_column_listview)

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager!!.offscreenPageLimit = 2

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager!!)

        notificationActivity = this

        optionTv.text = "お知らせ"
        backArrow.setOnClickListener {
            finish()
        }

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                print("")
            }

            override fun onPageSelected(position: Int) {
                viewPager!!.setCurrentItem(position, false)

            }

            override fun onPageScrollStateChanged(state: Int) {
                print("")

            }
        })

        getCurrentUser()?.let {
            currentUid = it.uid
            setDatas()
        }
    }

    fun initialize() {
        forYouNotificationList.clear()
        transactionNotificationList.clear()
        forYouPostList.clear()
        transactionPostList.clear()
        forYouUserList.clear()
        transactionUserList.clear()
        badgeCount_you = 0
        badgeCount_Transaction = 0
        usermodels.clear()
    }

    fun setDatas() {
        initialize()
        var countU = 0
        Api.User.observeUsers({ usermodel, userCount ->

            countU++
            usermodel?.let {
                usermodels.add(it)
            }

            if (userCount == countU) {

                var count = 0
                Api.Notification.observeMyNotifications(currentUid!!, { notification, notificationCount ->

                    if (notificationCount == null) {
                        setupViewPager(viewPager!!, null, null, null, null, null, null)

                        try {
                            setupTabIcons()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return@observeMyNotifications;
                    }


                    if (notification == null) {
                        count++
                    }

                    notification?.let { notification ->

                        //followはアンドロイドでは実装してないので、省く。
                        if (notification.type == "follow") {
                            count++

                        } else if (notification.type == "admin") {
                            count++
                            forYouNotificationList.add(0, notification)
                            //数あわせのため、追加
                            forYouPostList.add(0, Post())
                            forYouUserList.add(0, UserModel())
                            if (notification.checked == false) {
                                badgeCount_you++
                            }

                        } else {
                            Api.Post.observePost(notification.objectId!!, {

                                count++

                                it?.let { post ->

                                    val postId = post.id
                                    val segment = notification.segmentType
                                    val sellerShouldDo = post.sellerShouldDo
                                    val purchaserShouldDo = post.purchaserShouldDo


                                    if (notification.segmentType == getString(R.string.you)) {

                                        forYouNotificationList.add(0, notification)
                                        forYouPostList.add(0, post)
                                        if (notification.checked == false) {
                                            badgeCount_you++
                                        }
                                        usermodels.forEach({ usermodel ->
                                            if (usermodel.id == notification.from) {
                                                forYouUserList.add(0, usermodel)
                                            }
                                        })
                                    } else if (notification.segmentType == getString(R.string.transaction)) {
                                        transactionNotificationList.add(0, notification)
                                        transactionPostList.add(0, post)
                                        if (notification.checked == false) {
                                            badgeCount_Transaction++
                                        }
                                        usermodels.forEach({ usermodel ->
                                            if (usermodel.id == notification.from) {
                                                transactionUserList.add(0, usermodel)
                                            }
                                        })
                                    }

                                }

                                Log.d("count ", count.toString())
                                Log.d("notificationCount ", notificationCount.toString())
                                if (count == notificationCount) {
                                    setupViewPager(
                                        viewPager!!, forYouNotificationList,
                                        forYouPostList, forYouUserList,
                                        transactionNotificationList,
                                        transactionPostList, transactionUserList
                                    )

                                    try {
                                        setupTabIcons()
                                        badgeCountClear()

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                        }

                        Log.d("count ", count.toString())
                        Log.d("notificationCount ", notificationCount.toString())
                        if (count == notificationCount) {
                            setupViewPager(
                                viewPager!!, forYouNotificationList,
                                forYouPostList, forYouUserList,
                                transactionNotificationList,
                                transactionPostList, transactionUserList
                            )
                            try {
                                setupTabIcons()
                                badgeCountClear()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
            }
        })
    }

    //バッジカウントをクリアする。checkedをtrueにする。UI上で反映はされず、
    // アクティブティを再度onCreateしたときにcheckedがtrueになってるので、カウントが0になる。
    private fun badgeCountClear() {

        if (badgeCount_you != 0 || badgeCount_Transaction != 0) {
            Timer().schedule(2000, {

                forYouNotificationList.forEach { notification ->

                    if (!notification.checked!!) {

                        var result = HashMap<String, Any>()
                        result.put("checked", true)
                        Api.Notification.REF_NOTIFICATION.child(notification.id!!).updateChildren(result)
                    }
                }


                transactionNotificationList.forEach { notification ->

                    if (!notification.checked!!) {

                        var result = HashMap<String, Any>()
                        result.put("checked", true)
                        Api.Notification.REF_NOTIFICATION.child(notification.id!!).updateChildren(result)
                    }
                }

            })
        }
    }

    private fun setupViewPager(
        viewPager: ViewPager,
        forYouNotificationList: ArrayList<Notification>?,
        forYouPostList: ArrayList<Post>?,
        forYouUserList: ArrayList<UserModel>?,
        transactionNotificationList: ArrayList<Notification>?,
        transactionPostList: ArrayList<Post>?,
        transactionUserList: ArrayList<UserModel>?
    ) {

        val adapter = ViewPagerAdapter(supportFragmentManager)
        notificationFragment0 = NotificationFragment.newInstance(forYouNotificationList, forYouPostList, forYouUserList)
        notificationFragment1 =
                NotificationFragment.newInstance(transactionNotificationList, transactionPostList, transactionUserList)

        adapter.addFragment(notificationFragment0, tabTitle[0])
        adapter.addFragment(notificationFragment1, tabTitle[1])
        viewPager.adapter = adapter

    }


    private fun prepareTabView(pos: Int): View {
        val view = layoutInflater.inflate(R.layout.custom_tab, null)
        val tv_title = view.findViewById(R.id.titleTv) as TextView
        val tv_count = view.findViewById(R.id.countTv) as TextView
        tv_title.setText(tabTitle[pos])

        if (pos == 0) {
            if (badgeCount_you == 0) {
                tv_count.visibility = View.GONE
                return view
            }
            tv_count.visibility = View.VISIBLE
            tv_count.text = badgeCount_you.toString()

        } else if (pos == 1) {
            if (badgeCount_Transaction == 0) {
                tv_count.visibility = View.GONE
                return view
            }
            tv_count.visibility = View.VISIBLE
            tv_count.text = badgeCount_Transaction.toString()
        }

        return view
    }

    private fun setupTabIcons() {

        for (i in tabTitle.indices) {
            tabLayout?.getTabAt(i)?.setCustomView(prepareTabView(i))
        }

    }
}