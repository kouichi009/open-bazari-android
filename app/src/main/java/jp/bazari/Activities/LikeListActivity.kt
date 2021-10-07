package jp.bazari.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import jp.bazari.Adapters.ViewPagerAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Fragments.MyLikingPostsFragment
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.snippet_sell_list.*

class LikeListActivity : AppCompatActivity() {

    private val TAG = "LikeListActivity"

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null

    //Fragments
    lateinit var listFragment: MyLikingPostsFragment
    lateinit var listSoldOutFragment: MyLikingPostsFragment

    var tabTitle = arrayOf("販売中", "販売終了")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_column_listview)

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager!!.offscreenPageLimit = 2

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager!!)

        optionTv.text = "いいねした商品"
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

        var sellPostList: ArrayList<Post> = ArrayList()
        var soldPostList: ArrayList<Post> = ArrayList()

        var count = 0

        val currentUid = getCurrentUser()!!.uid

        Api.MyLikePosts.observeMyLikePosts(currentUid, {post, postCount ->

            if (postCount == 0) {
                setupViewPager(viewPager!!, null, null)

                try {
                    setupTabIcons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@observeMyLikePosts;
            }

            count++

            post?.let {

                if (it.transactionStatus == "sell") {
                    sellPostList.add(0,it)
                } else {
                    soldPostList.add(0,it)
                }

            }

            if (postCount == count) {
                setupViewPager(viewPager!!, sellPostList, soldPostList)

                try {
                    setupTabIcons()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })

    }



    private fun setupViewPager(viewPager: ViewPager, sellPostList: ArrayList<Post>?, soldPostList: ArrayList<Post>?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        listFragment = MyLikingPostsFragment.newInstance(sellPostList)
        listSoldOutFragment = MyLikingPostsFragment.newInstance(soldPostList)

        adapter.addFragment(listFragment, "CALLS")
        adapter.addFragment(listSoldOutFragment, "CHAT")
        viewPager.adapter = adapter
    }


    private fun prepareTabView(pos: Int): View {
        val view = layoutInflater.inflate(R.layout.custom_tab, null)
        val tv_title = view.findViewById(R.id.titleTv) as TextView
        val tv_count = view.findViewById(R.id.countTv) as TextView
        tv_title.setText(tabTitle[pos])

            tv_count.visibility = View.GONE


        return view
    }

    private fun setupTabIcons() {

        for (i in tabTitle.indices) {
            tabLayout?.getTabAt(i)?.setCustomView(prepareTabView(i))
        }
    }
}
