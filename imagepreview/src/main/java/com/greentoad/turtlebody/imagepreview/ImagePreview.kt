package com.greentoad.turtlebody.imagepreview

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.greentoad.turtlebody.imagepreview.ui.components.ImageAdapter
import com.greentoad.turtlebody.imagepreview.ui.components.ViewPagerAdapter
import kotlinx.android.synthetic.main.tb_image_preview_activity_lib_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.Serializable
import java.lang.ref.WeakReference


/**
 * Created by WANGSUN on 29-Mar-19.
 */
class ImagePreview {

    companion object {
        @JvmStatic
        fun with(activity: FragmentActivity): ImagePreviewImpl {
            return ImagePreviewImpl(activity)
        }

        private var mTopBottomBarIsVisible = true
    }


    class ImagePreviewImpl(activity: FragmentActivity) : PreviewDialogFragment.OnImagePreviewListener, AnkoLogger {


        private var mActivity: WeakReference<FragmentActivity> = WeakReference(activity)
        private var mUri: ArrayList<Uri> = arrayListOf()

        fun setUris(value: ArrayList<Uri>): ImagePreviewImpl{
            mUri = value
            return this
        }


        override fun onData(data: ArrayList<Uri>) {
        }

        override fun onCancel(message: String) {
        }

        override fun onPagerClick(isVisible: Boolean) {
            if(isVisible){
                mActivity.get()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                mActivity.get()?.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            else{
                mActivity.get()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                mActivity.get()?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }

        /**
         * @return observable uri list
         */
        fun onResult(){
            startFragment()
        }

        private fun startFragment() {
            hideDefaultToolbar()
            showDialog(mActivity.get()!!)
        }

        private fun hideDefaultToolbar() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mActivity.get()?.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }

            if(mActivity.get() is AppCompatActivity){
                (mActivity.get() as AppCompatActivity).supportActionBar?.hide()
            }
        }

        fun setOriginalState(){
            //todo: record original state before starting dialog
            //is activity full screen
            //is activity full screen with status
            //action bar exist?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(mTopBottomBarIsVisible)
                    mActivity.get()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                else
                    mActivity.get()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            if(mActivity.get() is AppCompatActivity){
                (mActivity.get() as AppCompatActivity).supportActionBar?.show()
            }
        }


        private fun showDialog(activity: FragmentActivity) {
            val bundle = Bundle()
            bundle.putSerializable("key", mUri as Serializable)

            val fragmentManager = activity.supportFragmentManager
            val newFragment = PreviewDialogFragment.newInstance(999,bundle)
            //newFragment.arguments = bundle
            newFragment.setListener(this)

            val transaction = fragmentManager.beginTransaction()
            transaction
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }


    /* **************************************
     *             Dialog  Fragment
     */
    class PreviewDialogFragment : DialogFragment(),AnkoLogger {
        private lateinit var mAdapterPager: ViewPagerAdapter
        private lateinit var mAdapterRecycler: ImageAdapter
        private var mList: ArrayList<Uri> = arrayListOf()

        companion object {

            @JvmStatic
            fun newInstance(key: Int, b: Bundle?): PreviewDialogFragment {
                val bf: Bundle = b ?: Bundle()
                bf.putInt("fragment.key", key);
                val fragment = PreviewDialogFragment()
                fragment.arguments = bf
                return fragment
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            if(arguments!=null){
                mList = arguments!!.getSerializable("key") as ArrayList<Uri>
            }
            return inflater.inflate(R.layout.tb_image_preview_activity_lib_main, container, false)
        }


        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            return dialog
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            tb_image_preview_toolbar_txt_count.text = "${mList.size}"
            initAdapter()
        }


        private fun initAdapter() {
            /*recycler view*/
            mAdapterRecycler = ImageAdapter()
            mAdapterRecycler.setData(mList)
            mAdapterRecycler.setListener(object : ImageAdapter.OnRecyclerImageClickListener{
                override fun onRecyclerImageClick(index: Int) {
                    tb_image_preview_viewpager.currentItem = index
                }
            })
            tb_image_preview_recyclerview_horizontal.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            tb_image_preview_recyclerview_horizontal.adapter = mAdapterRecycler

            /*view pager*/
            mAdapterPager = ViewPagerAdapter(childFragmentManager)
            mAdapterPager.setData(mList)
            mAdapterPager.setListener(object : ViewPagerAdapter.OnViewPagerClickListener{
                override fun onViewPagerClick() {
                    if(mTopBottomBarIsVisible){
                        info { "visible:" }
                        tb_image_preview_activity_toolbar.visibility = View.GONE
                        tb_image_preview_activity_lib_ll.visibility = View.GONE
                        mTopBottomBarIsVisible = false
                        mOnImagePreviewListener?.onPagerClick(false)
                    }
                    else{
                        tb_image_preview_activity_toolbar.visibility = View.VISIBLE
                        tb_image_preview_activity_lib_ll.visibility = View.VISIBLE
                        mTopBottomBarIsVisible = true
                        mOnImagePreviewListener?.onPagerClick(true)
                    }
                }
            })
            tb_image_preview_viewpager.adapter = mAdapterPager
            tb_image_preview_viewpager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    info { "page selected: $position" }
                    val currentSelected = mAdapterRecycler.getSelectedItem()
                    if (currentSelected != position) {
                        mAdapterRecycler.selectPosition(position)
                        if (currentSelected < position) {
                            tb_image_preview_recyclerview_horizontal.scrollToPosition(position)
                        } else {
                            if (position == 0) {
                                tb_image_preview_recyclerview_horizontal.scrollToPosition(position)
                            } else
                                tb_image_preview_recyclerview_horizontal.scrollToPosition(position)
                        }
                    }
                }
            })
        }

        private var mOnImagePreviewListener: OnImagePreviewListener? = null

        fun setListener(listener: OnImagePreviewListener) {
            this.mOnImagePreviewListener = listener
        }

        interface OnImagePreviewListener {
            fun onData(data: ArrayList<Uri>)
            fun onCancel(message: String)
            fun onPagerClick(isVisible: Boolean)
        }
    }
}