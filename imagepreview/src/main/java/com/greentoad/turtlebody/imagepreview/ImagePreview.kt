package com.greentoad.turtlebody.imagepreview

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.greentoad.turtlebody.imagepreview.core.ImagePreviewConfig
import com.greentoad.turtlebody.imagepreview.ui.components.FragmentBase
import com.greentoad.turtlebody.imagepreview.ui.components.ImageAdapter
import com.greentoad.turtlebody.imagepreview.ui.components.ViewPagerAdapter
import kotlinx.android.synthetic.main.tb_image_preview_fragment_preview.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.Serializable
import java.lang.ref.WeakReference
import android.content.Context.WINDOW_SERVICE
import android.content.pm.ActivityInfo
import androidx.core.content.ContextCompat.getSystemService
import android.view.WindowManager
import android.view.Display
import androidx.fragment.app.DialogFragment


/**
 * Created by WANGSUN on 29-Mar-19.
 */
class ImagePreview {

    companion object {
        @JvmStatic
        fun with(activity: FragmentActivity): ImagePreviewImpl {
            return ImagePreviewImpl(activity)
        }
    }


    class ImagePreviewImpl(activity: FragmentActivity) : PreviewFragment.OnPreviewFragmentListener, AnkoLogger {

        private var mNavigationalBarColor: Int? = null
        private var mOriginalFlag: Int? = null
        private var mStatusBarColor: Int? = null
        private var mIsActionBarShowing: Boolean? = null
        private var mFragment: DialogFragment? = null

        private var mPreviewConfig: ImagePreviewConfig = ImagePreviewConfig()

        private var mActivity: WeakReference<FragmentActivity> = WeakReference(activity)

        /**
         * @param value: ImagePreviewConfig
         */
        fun setConfig(value: ImagePreviewConfig): ImagePreviewImpl{
            mPreviewConfig = value
            return this
        }


        override fun onDone(data: ArrayList<Uri>) {
            mOnImagePreviewListener?.onDone(data)
        }

        override fun onAddBtnClicked() {
            mOnImagePreviewListener?.onAddBtnClicked()
        }

        override fun onBackPressed() {
            setOriginalState()
        }

        fun dismissImagePreview(){
            if(mFragment!=null){
                setOriginalState()
                mActivity.get()?.supportFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                mFragment = null
            }
        }

        /**
         * start image-preview fragment
         */
        fun start() {
            hideDefaultToolbar()
            showDialog(mActivity.get()!!)
        }


        private fun hideDefaultToolbar() {
            // flag = mActivity.get()?.window?.decorView?.systemUiVisibility
            mActivity.get()?.let {
                if (it is AppCompatActivity) {
                    if(it.supportActionBar == null){
                        mIsActionBarShowing = null
                    }
                    else{
                        mIsActionBarShowing = it.supportActionBar?.isShowing
                        it.supportActionBar?.hide()
                    }
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    it.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                    mNavigationalBarColor = it.window.navigationBarColor
                    it.window.navigationBarColor = ContextCompat.getColor(it,R.color.md_black_1000_75)

                    //status bar
                    mStatusBarColor = it.window.statusBarColor
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        mOriginalFlag = it.window.decorView.systemUiVisibility
                    }
                    initStatusBar()
                }
            }
        }

        private fun setOriginalState() {
            mActivity.get()?.let {
                if (it is AppCompatActivity) {
                    if(mIsActionBarShowing!=null){
                        if(mIsActionBarShowing!!){
                            it.supportActionBar?.show()
                        }
                    }
                }

                it.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    if(mNavigationalBarColor!=null)
                        it.window.navigationBarColor = mNavigationalBarColor!!

                    //status
                    if(mStatusBarColor!=null)
                        it.window.statusBarColor = mStatusBarColor!!
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        if(mOriginalFlag!=null)
                            it.window.decorView.systemUiVisibility = mOriginalFlag!!
                        if(mStatusBarColor!=null)
                            it.window.statusBarColor = mStatusBarColor!!
                    }
                }
            }
        }

        private fun initStatusBar() {
            mActivity.get()?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.window.statusBarColor = ContextCompat.getColor(it, R.color.md_black_1000)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var flags = it.window.decorView.systemUiVisibility
                    flags = flags and  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    it.window.decorView.systemUiVisibility = flags
                    it.window.statusBarColor = Color.BLACK
                }
            }
        }


        private fun showDialog(activity: FragmentActivity) {
            val bundle = Bundle()
            bundle.putSerializable(ImagePreviewConfig.ARG_BUNDLE, mPreviewConfig as Serializable)

            val fragmentManager = activity.supportFragmentManager
            val newFragment = PreviewFragment.newInstance(999, bundle)
            newFragment.setListener(this)

            val transaction = fragmentManager.beginTransaction()
            transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()

            mFragment = newFragment
        }


        /*********************
         *    Listener
         **********************/

        private var mOnImagePreviewListener: OnImagePreviewListener? = null

        fun setListener(fragmentListener: OnImagePreviewListener): ImagePreviewImpl {
            this.mOnImagePreviewListener = fragmentListener
            return this
        }

        interface OnImagePreviewListener {
            fun onDone(data: ArrayList<Uri>)
            fun onAddBtnClicked()
        }
    }


    /* **************************************
     *             Dialog  Fragment
     */
    class PreviewFragment : FragmentBase(){


        private lateinit var mAdapterPager: ViewPagerAdapter
        private lateinit var mAdapterRecycler: ImageAdapter
        private var mTopBottomBarIsVisible = true
        private var mPreviewConfig: ImagePreviewConfig = ImagePreviewConfig()

        companion object {
            @JvmStatic
            fun newInstance(key: Int, b: Bundle?): PreviewFragment {
                val bf: Bundle = b ?: Bundle()
                bf.putInt("fragment.key", key);
                val fragment = PreviewFragment()
                fragment.arguments = bf
                return fragment
            }

            private var mUiVisibilityFlag: Int? = 0
        }


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            if (arguments != null) {
                mPreviewConfig = arguments!!.getSerializable(ImagePreviewConfig.ARG_BUNDLE) as ImagePreviewConfig
            }

            val view =  inflater.inflate(R.layout.tb_image_preview_fragment_preview, container, false)

            view.isFocusableInTouchMode = true
            view.requestFocus()
            view.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                    info { "back pressed222" }
                    if (keyCode == KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                        info { "back pressed" }
                        onBackPressed()
                        return true
                    }
                    return false
                }
            })
            return view
        }


        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            return dialog
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            preview_fragment_activity_toolbar.navigationIcon = ContextCompat.getDrawable(context!!,R.drawable.tb_image_preview_ic_arrow_back_white_24dp)
            preview_fragment_toolbar_txt_count.text = "${mPreviewConfig.mUriList.size}"

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                preview_fragment_app_bar.setPadding(0,getStatusBarHeight(),0,0)
                preview_fragment_bottom_ll.setPadding(0,0,0,getNavigationBarSize(context!!).y)
            }

            initButton()
            initAdapter()
        }

        private fun initButton() {
            preview_fragment_main_add_btn.setOnClickListener {
                mOnPreviewFragmentListener?.onAddBtnClicked()
            }
            preview_fragment_iv_done.setOnClickListener {
                mOnPreviewFragmentListener?.onDone(mPreviewConfig.getUris())
                onBackPressed()
            }

            preview_fragment_activity_toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            if(mPreviewConfig.mAllowAddButton){
                preview_fragment_main_vertical_line.visibility = View.VISIBLE
                preview_fragment_main_add_btn.visibility = View.VISIBLE
            }
            else{
                preview_fragment_main_vertical_line.visibility = View.GONE
                preview_fragment_main_add_btn.visibility = View.GONE
            }

            if(mPreviewConfig.mDisplayThumbnailLowerBar){
                preview_fragment_bottom_ll.visibility = View.VISIBLE
            }else{
                preview_fragment_bottom_ll.visibility = View.GONE
            }
        }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.test_menu, menu)
        }


        private fun onBackPressed(){
            mOnPreviewFragmentListener?.onBackPressed()
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        private fun show() {
            mUiVisibilityFlag?.let {
                preview_fragment_parent_fl.systemUiVisibility = it

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    preview_fragment_app_bar.setPadding(0,getStatusBarHeight(),0,0)
                    preview_fragment_bottom_ll.setPadding(0,0,0,getNavigationBarSize(context!!).y)
                }

                preview_fragment_activity_toolbar.visibility = View.VISIBLE

                if(mPreviewConfig.mUriList.size>1) {
                    preview_fragment_bottom_ll.visibility = View.VISIBLE
                }
            }
        }

        private fun hide() {
            preview_fragment_parent_fl?.let {
                mUiVisibilityFlag = preview_fragment_parent_fl.systemUiVisibility
                preview_fragment_parent_fl.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            //View.SYSTEM_UI_FLAG_LAYOUT_STABLE or //to get stable view this mUiVisibilityFlag sometime add views which disrupt our original views
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                preview_fragment_app_bar.setPadding(0, 0, 0, 0)
                preview_fragment_bottom_ll.setPadding(0, 0, 0, 0)

                preview_fragment_activity_toolbar.visibility = View.GONE

                if (mPreviewConfig.mUriList.size > 1) {
                    preview_fragment_bottom_ll.visibility = View.GONE
                }
            }
        }

        private fun initAdapter() {
            /*recycler view*/
            mAdapterRecycler = ImageAdapter()
            mAdapterRecycler.setData(mPreviewConfig.getUris())
            mAdapterRecycler.setListener(object : ImageAdapter.OnRecyclerImageClickListener {
                override fun onRecyclerImageClick(index: Int) {
                    preview_fragment_viewpager.currentItem = index
                }
            })
            preview_fragment_recyclerview_horizontal.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            preview_fragment_recyclerview_horizontal.adapter = mAdapterRecycler

            /*view pager*/
            mAdapterPager = ViewPagerAdapter(childFragmentManager)
            mAdapterPager.setData(mPreviewConfig.getUris())
            mAdapterPager.setListener(object : ViewPagerAdapter.OnViewPagerClickListener {
                override fun onViewPagerClick() {
                    mTopBottomBarIsVisible = if (mTopBottomBarIsVisible) {
                        try {
                            hide()
                        }catch (e:IllegalStateException){
                        }catch (e:NullPointerException){
                        }
                        false
                    } else {
                        show()
                        true
                    }
                }
            })
            preview_fragment_viewpager.adapter = mAdapterPager
            preview_fragment_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    info { "page selected: $position" }
                    val currentSelected = mAdapterRecycler.getSelectedItem()
                    if (currentSelected != position) {
                        mAdapterRecycler.selectPosition(position)
                        if (currentSelected < position) {
                            preview_fragment_recyclerview_horizontal.scrollToPosition(position)
                        } else {
                            if (position == 0) {
                                preview_fragment_recyclerview_horizontal.scrollToPosition(position)
                            } else
                                preview_fragment_recyclerview_horizontal.scrollToPosition(position)
                        }
                    }
                }
            })
        }


        /*********************
         *    Listener
         **********************/

        private var mOnPreviewFragmentListener: OnPreviewFragmentListener? = null

        fun setListener(fragmentListener: OnPreviewFragmentListener) {
            this.mOnPreviewFragmentListener = fragmentListener
        }

        interface OnPreviewFragmentListener {
            fun onDone(data: ArrayList<Uri>)
            fun onBackPressed()
            fun onAddBtnClicked()
        }
    }
}