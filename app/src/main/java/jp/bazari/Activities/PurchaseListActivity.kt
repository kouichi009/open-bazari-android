package jp.bazari.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import jp.bazari.Adapters.ViewPagerAdapter
import jp.bazari.Apis.*
import jp.bazari.Fragments.PurchaseListFragment
import jp.bazari.Fragments.PurchaseListSoldOutFragment
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.snippet_sell_list.*

var purchaseListActivity: PurchaseListActivity? = null

class PurchaseListActivity : AppCompatActivity(), PurchaseListActivityInterface {

    override fun updatePurchaseListAC() {
        setDatas()
    }

    private val TAG = "PurchaseListActivity"

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null

    //Fragments
    lateinit var listFragment: PurchaseListFragment
    lateinit var listSoldOutFragment: PurchaseListSoldOutFragment

    var tabTitle = arrayOf("取引中", "購入済")
    var badgeCount = 0

    var transactionPostList: ArrayList<Post> = ArrayList()
    var soldoutPostList: ArrayList<Post> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_column_listview)

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager!!.offscreenPageLimit = 2

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager!!)

        purchaseListActivity = this

        optionTv.text = "購入した商品"
        backArrow.setOnClickListener {
            finish()
        }

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewPager!!.setCurrentItem(position, false)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        setDatas()

    }

    fun initialize() {
        transactionPostList.clear()
        soldoutPostList.clear()

        badgeCount = 0
    }

    fun setDatas() {
        initialize()

        var count = 0
        Api.MyPurchasePosts.observeMyPurchasePosts(getCurrentUser()!!.uid, { post, postCount ->

            if (postCount == 0) {
                setupViewPager(viewPager!!, null, null)

                try {
                    setupTabIcons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@observeMyPurchasePosts;
            }

            count++

            post?.let {
                if (it.purchaserShouldDo == waitForShip || it.purchaserShouldDo == catchProduct || it.purchaserShouldDo == waitForValue) {
                    transactionPostList.add(0,it)

                    if (it.purchaserShouldDo == catchProduct) {
                        badgeCount++
                    }
                }

                if (it.purchaserShouldDo == buyFinish) {
                    soldoutPostList.add(0,it)
                }
            }

            if (postCount == count) {
                setupViewPager(viewPager!!, transactionPostList, soldoutPostList)

                try {
                    setupTabIcons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }


    private fun setupViewPager(viewPager: ViewPager, transactionPostList: ArrayList<Post>?, soldoutPostList: ArrayList<Post>?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        listFragment = PurchaseListFragment.newInstance(transactionPostList)
        listSoldOutFragment = PurchaseListSoldOutFragment.newInstance(soldoutPostList)

        adapter.addFragment(listFragment, "CALLS")
        adapter.addFragment(listSoldOutFragment, "CHAT")
        viewPager.adapter = adapter
    }


    private fun prepareTabView(pos: Int): View {
        val view = layoutInflater.inflate(R.layout.custom_tab, null)
        val tv_title = view.findViewById(R.id.titleTv) as TextView
        val tv_count = view.findViewById(R.id.countTv) as TextView
        tv_title.setText(tabTitle[pos])

        if (pos == 1) {
            tv_count.visibility = View.GONE
            return view
        }

        if (badgeCount > 0) {
            tv_count.visibility = View.VISIBLE
            tv_count.text = badgeCount.toString()
        } else
            tv_count.visibility = View.GONE


        return view
    }

    private fun setupTabIcons() {

        for (i in tabTitle.indices) {
            tabLayout?.getTabAt(i)?.setCustomView(prepareTabView(i))
        }
    }
}
