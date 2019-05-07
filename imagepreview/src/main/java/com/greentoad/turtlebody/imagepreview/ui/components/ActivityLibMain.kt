package com.greentoad.turtlebody.imagepreview.ui.components

import android.app.Dialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.greentoad.turtlebody.imagepreview.R
import kotlinx.android.synthetic.main.tb_image_preview_activity_lib_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ActivityLibMain : AppCompatActivity(),AnkoLogger {

    private lateinit var mPagerAdapter: ViewPagerAdapter
    private lateinit var mImageAdapter: ImageAdapter

    private var mList: ArrayList<Uri> = arrayListOf()

    companion object{
        const val B_ARG_URI_LIST = "activity.lib.main.uri.list"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tb_image_preview_activity_lib_main)

        if(intent.extras!=null){
            mList = intent.getSerializableExtra(B_ARG_URI_LIST) as ArrayList<Uri>
        }
        initAdapter()
    }

    private fun initAdapter() {
        mImageAdapter = ImageAdapter()
        mImageAdapter.setData(mList)
        tb_image_preview_recyclerview_horizontal.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        tb_image_preview_recyclerview_horizontal.adapter = mImageAdapter

        mPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mPagerAdapter.setData(mList)
        tb_image_preview_viewpager.adapter = mPagerAdapter
        tb_image_preview_viewpager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                info { "page selected: $position" }
            }
        })

    }

    fun ww(){

       // Window
        //var phw = PhoneWindow()
//        DialogFragment
//        Dialog
//        var a = PhoneWindow
    }
}
