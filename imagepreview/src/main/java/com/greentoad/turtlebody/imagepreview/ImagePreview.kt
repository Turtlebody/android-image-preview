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
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR




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


    class ImagePreviewImpl(activity: FragmentActivity) : PreviewFragment.OnImagePreviewListener, AnkoLogger {

        private var flag: Int = 0
        private var mNavigationalBarColor: Int = 0
        private var mOriginalFlag: Int = 0
        private var mStatusBarColor: Int = 0
        private var mPreviewConfig: ImagePreviewConfig = ImagePreviewConfig()

        private var mActivity: WeakReference<FragmentActivity> = WeakReference(activity)
        private var mUri: ArrayList<Uri> = arrayListOf()

        fun setConfig(value: ImagePreviewConfig): ImagePreviewImpl{
            mPreviewConfig = value
            return this
        }

        fun setUris(value: ArrayList<Uri>): ImagePreviewImpl {
            mUri = value
            return this
        }

        override fun onDone(data: ArrayList<Uri>) {
        }

        override fun onAddBtnClicked() {}

        override fun onBackPressed() {
            setOriginalState()
        }

//        override fun onPagerClicked(isVisible: Boolean) {
//            if (isVisible) { show() }
//            else { hide() }
//        }

        private fun show() {
            mActivity.get()?.window?.decorView?.systemUiVisibility = flag or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        private fun hide() {
            mActivity.get()?.let {
                flag = it.window.decorView.systemUiVisibility
                it.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }

        }

        /**
         * @return observable uri list
         */
        fun onResult() {
            startFragment()
        }

        private fun startFragment() {
            hideDefaultToolbar()
            showDialog(mActivity.get()!!)
        }



        private fun hideDefaultToolbar() {
           // flag = mActivity.get()?.window?.decorView?.systemUiVisibility

            mActivity.get()?.let {
                if (it is AppCompatActivity) {
                    it.supportActionBar?.hide()
                }

                it.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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
                    it.supportActionBar?.show()
                }
                it.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    it.window.navigationBarColor = mNavigationalBarColor

                    //status
                    it.window.statusBarColor = mStatusBarColor
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        it.window.decorView.systemUiVisibility = mOriginalFlag
                        it.window.statusBarColor = mStatusBarColor
                    }
                }
            }
//            flag?.let {
//                mActivity.get()?.window?.decorView?.systemUiVisibility = it or
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            }
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
            bundle.putSerializable("key", mUri as Serializable)
            bundle.putSerializable("key2", mPreviewConfig as Serializable)

            val fragmentManager = activity.supportFragmentManager
            val newFragment = PreviewFragment.newInstance(999, bundle)
            newFragment.setListener(this)

            val transaction = fragmentManager.beginTransaction()
            transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }


    /* **************************************
     *             Dialog  Fragment
     */
    class PreviewFragment : FragmentBase(){


        private lateinit var mAdapterPager: ViewPagerAdapter
        private lateinit var mAdapterRecycler: ImageAdapter
        private var mList: ArrayList<Uri> = arrayListOf()
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
                mList = arguments!!.getSerializable("key") as ArrayList<Uri>
                mPreviewConfig = arguments!!.getSerializable("key2") as ImagePreviewConfig
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

            preview_fragment_activity_toolbar.navigationIcon = ContextCompat.getDrawable(context!!,R.drawable.ic_arrow_back_white_24dp)
            preview_fragment_toolbar_txt_count.text = "${mList.size}"

            preview_fragment_app_bar.setPadding(0,getStatusBarHeight(),0,0)
            preview_fragment_bottom_ll.setPadding(0,0,0,getNavigationBarSize(context!!).y)

            initButton()
            initAdapter()
        }

        private fun initButton() {
            preview_fragment_main_add_btn.setOnClickListener {
                mOnImagePreviewListener?.onAddBtnClicked()
            }
            preview_fragment_iv_done.setOnClickListener {
                mOnImagePreviewListener?.onDone(mList)
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
        }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.test_menu, menu)
        }


        private fun onBackPressed(){
            mOnImagePreviewListener?.onBackPressed()
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        private fun show() {
            mUiVisibilityFlag?.let {
                preview_fragment_parent_fl.systemUiVisibility = it
                preview_fragment_app_bar.setPadding(0,getStatusBarHeight(),0,0)
                preview_fragment_bottom_ll.setPadding(0,0,0,getNavigationBarSize(context!!).y)

                preview_fragment_activity_toolbar.visibility = View.VISIBLE
                preview_fragment_bottom_ll.visibility = View.VISIBLE
            }
        }

        private fun hide() {
            mUiVisibilityFlag = preview_fragment_parent_fl.systemUiVisibility
            preview_fragment_parent_fl.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                       //View.SYSTEM_UI_FLAG_LAYOUT_STABLE or //to get stable view this mUiVisibilityFlag sometime add views which disrupt our original views
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

            preview_fragment_app_bar.setPadding(0,0,0,0)
            preview_fragment_bottom_ll.setPadding(0,0,0,0)

            preview_fragment_activity_toolbar.visibility = View.GONE
            preview_fragment_bottom_ll.visibility = View.GONE
        }

        private fun initAdapter() {
            /*recycler view*/
            mAdapterRecycler = ImageAdapter()
            mAdapterRecycler.setData(mList)
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
            mAdapterPager.setData(mList)
            mAdapterPager.setListener(object : ViewPagerAdapter.OnViewPagerClickListener {
                override fun onViewPagerClick() {
                    if (mTopBottomBarIsVisible) {
                        hide()
                        mTopBottomBarIsVisible = false
                        //mOnImagePreviewListener?.onPagerClicked(false)
                    } else {
                        show()
                        mTopBottomBarIsVisible = true
                        //mOnImagePreviewListener?.onPagerClicked(true)
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

        private var mOnImagePreviewListener: OnImagePreviewListener? = null

        fun setListener(listener: OnImagePreviewListener) {
            this.mOnImagePreviewListener = listener
        }

        interface OnImagePreviewListener {
            fun onDone(data: ArrayList<Uri>)
            fun onBackPressed()
            //fun onPagerClicked(isVisible: Boolean)
            fun onAddBtnClicked()
        }
    }
}