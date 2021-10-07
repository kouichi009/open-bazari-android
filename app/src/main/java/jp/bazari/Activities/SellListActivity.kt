package jp.bazari.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import jp.bazari.Adapters.ViewPagerAdapter
import jp.bazari.Apis.*
import jp.bazari.Fragments.SellListFragment
import jp.bazari.Fragments.SellListSoldOutFragment
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.snippet_sell_list.*

var sellListActivity: SellListActivity? = null

class SellListActivity : AppCompatActivity(), SellListActivityInterface {

    override fun updateSellListAC() {
        setDatas()
    }

    private val TAG = "SellListActivity"

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null

    //Fragments
    lateinit var listFragment: SellListFragment
    lateinit var listSoldOutFragment: SellListSoldOutFragment

    var tabTitle = arrayOf("取引中", "売却済")
    var badgeCount = 0


    var transactionPostList: ArrayList<Post> = ArrayList()
    var soldoutPostList: ArrayList<Post> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_column_listview)

        sellListActivity = this

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager!!.offscreenPageLimit = 2

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager!!)

        optionTv.text = "販売した商品"
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
        Api.MySellPosts.observeMySellPosts(getCurrentUser()!!.uid, { post, postCount ->

            if (postCount == 0) {
                setupViewPager(viewPager!!, null, null)

                try {
                    setupTabIcons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@observeMySellPosts;
            }

            count++

            post?.let {

                if (it.sellerShouldDo == ship || it.sellerShouldDo == waitCatch || it.sellerShouldDo == valueBuyer) {
                    transactionPostList.add(0,it)

                    if (it.sellerShouldDo == ship || it.sellerShouldDo == valueBuyer) {
                        badgeCount++
                    }
                }

                if (it.sellerShouldDo == soldFinish) {
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
        listFragment = SellListFragment.newInstance(transactionPostList)
        listSoldOutFragment = SellListSoldOutFragment.newInstance(soldoutPostList)

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
