package jp.bazari.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigkoo.svprogresshud.SVProgressHUD
import jp.bazari.Adapters.RecyclerViewAdapter
import jp.bazari.Apis.Api
import jp.bazari.Apis.increaseNumDefault
import jp.bazari.Apis.pageDefault
import jp.bazari.R
import jp.bazari.models.Post
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*
import kotlin.concurrent.schedule




class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    lateinit var mContext: Context
    var posts: ArrayList<Post> = ArrayList()
    var onePostFlg = false
    var page = 0
    var increaseNum = 0
    var reloadAllPostFlg = false

    var recyclerViewAdapter: RecyclerViewAdapter? = null

    var isLoading = false

    var mSVProgressHUD: SVProgressHUD? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        context.let {
            mContext = it!!
            mSVProgressHUD = SVProgressHUD(mContext)
        }
    }

    fun initialize(view: View) {

        //初期化
        posts.clear()
        page = pageDefault
        increaseNum = increaseNumDefault
        onePostFlg = false
        isLoading = false

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        initialize(view)

        val size = posts.size
        getPosts {

            if (it) {
                setAdapter(view)
            }
        }

        return view
    }

    fun setAdapter(view: View) {

        recyclerViewAdapter = RecyclerViewAdapter(posts,mContext)
        view.recyclerView.layoutManager = GridLayoutManager(mContext, 2)
        view.recyclerView.adapter = recyclerViewAdapter
        view.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {


            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // only when scrolling up

                    val visibleThreshold = 2

                    val layoutManager = view.recyclerView.getLayoutManager() as GridLayoutManager
                    val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    val currentTotalCount = layoutManager.itemCount

                    if (currentTotalCount <= lastItem + visibleThreshold) {
                        //show your loading view
                        // load content in background
                        loadMorePosts()
                    }
                }
            }
        })

    }


    fun getPosts(completed: (Boolean) -> Unit) {


        var count = 0

        Api.Post.fetchCountPosts { postsCount ->


            Api.Post.observePostsAdded { post, type ->

                if (onePostFlg) {

                    post?.let {
                        when (type) {

                            "add" -> {
                                posts.add(0, it)
                                recyclerViewAdapter?.notifyDataSetChanged()
                            }

                            "change" -> {

                                val iter = posts.listIterator()
                                while (iter.hasNext()) {
                                    val pos = iter.next()
                                    if (pos.id.equals(it.id)) {
                                        iter.set(it)
                                        val title = it.title
                                        recyclerViewAdapter?.notifyDataSetChanged()
                                        return@observePostsAdded
                                    }
                                }
                            }

                            "remove" -> {

                                val iter = posts.iterator()

                                while (iter.hasNext()) {
                                    val str = iter.next()

                                    if (str.id == it.id) {
                                        iter.remove()
                                        val a = str.title
                                        recyclerViewAdapter?.notifyDataSetChanged()
                                        return@observePostsAdded
                                    }
                                }

                            }
                            else -> {

                            }
                        }


                    }
                    completed(true)
                    return@observePostsAdded

                }

                count++

                if (postsCount < page) {

                    post?.let {
                        posts.add(0, it)
                    }

                    if (postsCount == count) {
                        onePostFlg = true
                        completed(true)
                    }
                    return@observePostsAdded
                }

                val ptCount = postsCount - page

                if (count > ptCount && ptCount >= 0) {

                    post?.let {
                        posts.add(0, it)
                    }

                    if (count == postsCount) {
                        val nowCount = posts.size
                        onePostFlg = true
                        completed(true)
                    }
                }
            }
        }
    }

    fun loadMorePosts() {

        //prePage
        var prepage = page
        var count = 0

        // increase page size
        page = page + increaseNum


        Api.Post.observePosts(page, {post, postsCount ->

            //DatabaseにあるすべてのPostをロードした後に走る。
            if (prepage >= postsCount) {
                page = postsCount
                prepage = postsCount
                isLoading = false
                reloadAllPostFlg = true
                return@observePosts
            }

            //DatabaseにあるすべてのPostをロードした後、新しくPostされたら走る
            if (reloadAllPostFlg) {


                mSVProgressHUD?.showWithStatus("少々おまちください。")
                count += 1

                if (count == 1) {
                    posts.clear()
                }

                post?.let {
                    posts.add(post)
                }

                if (postsCount == count) {

                    mSVProgressHUD?.dismiss()
                    Timer().schedule(1000, {

                        activity!!.runOnUiThread {
                            mSVProgressHUD?.showSuccessWithStatus("読み込み完了", SVProgressHUD.SVProgressHUDMaskType.None)
                        }
                    })
                    reloadAllPostFlg = false
                    return@observePosts
                } else {
                    return@observePosts
                }
            }

            mSVProgressHUD?.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.None)

            count++

            if (count > prepage) {

                post?.let {
                    val title =  it.title
                    posts.add(it)
                }
            }

            if (count == postsCount) {

               recyclerViewAdapter?.notifyDataSetChanged()
                Log.d(TAG, "loadFinish")
                isLoading = false
                mSVProgressHUD?.dismiss()
            }
        })
    }
}
