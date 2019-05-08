package com.greentoad.turtlebody.imagepreview.ui.components

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.greentoad.turtlebody.imagepreview.R
import kotlinx.android.synthetic.main.tb_image_preview_fragment_preview.*
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
        setContentView(R.layout.tb_image_preview_fragment_preview)

        if(intent.extras!=null){
            mList = intent.getSerializableExtra(B_ARG_URI_LIST) as ArrayList<Uri>
        }
        initAdapter()
    }

    private fun initAdapter() {
        mImageAdapter = ImageAdapter()
        mImageAdapter.setData(mList)
        preview_fragment_recyclerview_horizontal.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        preview_fragment_recyclerview_horizontal.adapter = mImageAdapter

        mPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mPagerAdapter.setData(mList)
        preview_fragment_viewpager.adapter = mPagerAdapter
        preview_fragment_viewpager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                info { "page selected: $position" }
            }
        })

    }
}
