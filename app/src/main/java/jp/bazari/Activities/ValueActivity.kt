package jp.bazari.Activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import jp.bazari.Adapters.ViewPagerAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.getCurrentUser
import jp.bazari.Fragments.ValueAllFragment
import jp.bazari.Fragments.ValuePurchaseFragment
import jp.bazari.Fragments.ValueSellFragment
import jp.bazari.R
import jp.bazari.models.UserModel
import jp.bazari.models.ValueModel
import kotlinx.android.synthetic.main.snippet_sell_list.*

class ValueActivity : AppCompatActivity() {

    private val TAG = "ValueActivity"

    var currentUid: String? = null

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null

    //Fragments
    lateinit var valueAllFragment: ValueAllFragment
    lateinit var valuePurchaseFragment: ValuePurchaseFragment
    lateinit var valueSellFragment: ValueSellFragment

    var SELL = "sell"
    var PURCHASE = "purchase"

    var userFlg = false
    var valueFlg = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_column_listview)

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        viewPager!!.offscreenPageLimit = 3

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager!!)

        currentUid = getCurrentUser()!!.uid

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

        var allValueList: ArrayList<ValueModel> = ArrayList()
        var sellValueList: ArrayList<ValueModel> = ArrayList()
        var purchaseValueList: ArrayList<ValueModel> = ArrayList()
        var usermodels: ArrayList<UserModel> = ArrayList()

        var count = 0
        var userCount = 0
        Api.Value.fetchMyValues(currentUid!!, { valueModel, valueCount ->

            if (valueCount == 0) {
                setupViewPager(viewPager!!, null, null, null, null)


                return@fetchMyValues
            }
            count++

            valueModel?.let {valuemodel ->

                Api.User.observeUser(valuemodel.from!!, { usermodel ->

                    userCount++
                    usermodel?.let {
                        usermodels.add(0, it)
                    }

                    Log.d("ValueCount", valueCount.toString())
                    Log.d("UserCount", userCount.toString())
                    if (valueCount == userCount) {
                        userFlg = true
                        isCompletedFirebase(viewPager!!, allValueList, sellValueList, purchaseValueList, usermodels)
                    }
                })

                allValueList.add(0, valuemodel)

                if (valuemodel.type == SELL) {
                    sellValueList.add(0, valuemodel)
                } else if (valuemodel.type == PURCHASE) {
                    purchaseValueList.add(0, valuemodel)
                }
            }
            if(valueCount == count) {
                valueFlg = true
                isCompletedFirebase(viewPager!!, allValueList, sellValueList, purchaseValueList, usermodels)
            }
        })

    }

    fun isCompletedFirebase(viewPager: ViewPager, allValueList: ArrayList<ValueModel>?,
                            sellValueList: ArrayList<ValueModel>?, purchaseValueList: ArrayList<ValueModel>?,
                            usermodels: ArrayList<UserModel>?) {

        if (userFlg && valueFlg) {
            setupViewPager(viewPager, allValueList, sellValueList, purchaseValueList, usermodels)

        }
    }



    private fun setupViewPager(viewPager: ViewPager, allValueList: ArrayList<ValueModel>?,
                               sellValueList: ArrayList<ValueModel>?, purchaseValueList: ArrayList<ValueModel>?,
                               usermodels: ArrayList<UserModel>?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        valueAllFragment = ValueAllFragment.newInstance(allValueList, usermodels)
        valuePurchaseFragment = ValuePurchaseFragment.newInstance(purchaseValueList, usermodels)
        valueSellFragment = ValueSellFragment.newInstance(sellValueList, usermodels)

        adapter.addFragment(valueAllFragment, "すべて")
        adapter.addFragment(valuePurchaseFragment, "販売")
        adapter.addFragment(valueSellFragment, "購入")
        viewPager.adapter = adapter
    }

}
