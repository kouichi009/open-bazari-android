package jp.bazari.Adapters

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.bazari.Activities.detailActivity
import jp.bazari.Activities.detailPost
import jp.bazari.R
import kotlinx.android.synthetic.main.multi_image.*


class ImagesAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position)
    }

    override fun getCount(): Int {

        detailPost?.let {
            return it.imageCount!!
        }
        return 0
    }
}

class PlaceholderFragment : Fragment() {

    lateinit var mContext: Context

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        detailPost = null
        detailActivity = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.multi_image, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateUI()
    }

    //Picassoが表示されないことがある。ProfileUserActivityからDetailActivityを開いたときにおきることがある。
    //ProfileUserActivityはPaginationせずに一気に全部のPostを表示するため、メモリをいっぱい使って、Memoryouterrorを起こす。
    //30PostぐらいまではOK100を越えるとエラーになる。
    private fun updateUI() {
        val mPicasso: Picasso = Picasso.get()

        if (arguments!!.getInt(ARG_SECTION_NUMBER) == 0) {

            mPicasso
                .load(Uri.parse(detailPost!!.imageUrls[0]))
                .into(imageView, object : Callback {

                    override fun onSuccess() {
                        print("success")
                    }


                    override fun onError(e: Exception?) {
                        val err = e?.printStackTrace()
                    }
                })

        }
        if (arguments!!.getInt(ARG_SECTION_NUMBER) == 1) {
            mPicasso
                .load(Uri.parse(detailPost!!.imageUrls[1]))
                .into(imageView)
        }
        if (arguments!!.getInt(ARG_SECTION_NUMBER) == 2) {
            mPicasso
                .load(Uri.parse(detailPost!!.imageUrls[2]))
                .into(imageView)
        }

        if (arguments!!.getInt(ARG_SECTION_NUMBER) == 3) {
            mPicasso
                .load(Uri.parse(detailPost!!.imageUrls[3]))
                .into(imageView)
        }

    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}