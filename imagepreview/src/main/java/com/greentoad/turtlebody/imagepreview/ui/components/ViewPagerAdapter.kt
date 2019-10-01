package com.greentoad.turtlebody.imagepreview.ui.components

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by WANGSUN on 02-May-19.
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    companion object {
        const val B_ARG_URI = "image.adapter.view.pager.uri"
    }

    private var mData: ArrayList<Uri> = arrayListOf()

    override fun getItem(position: Int): ImageFragmentViewer {
        val b = Bundle()
        b.putString(B_ARG_URI, mData[position].toString())

        return ImageFragmentViewer.newInstance(position, b).apply {
            setListener(
                object : ImageFragmentViewer.OnImageClickListener {
                    override fun onImageClick() {
                        mOnViewPagerClickListener?.onViewPagerClick()
                    }
                }
            )
        }
    }

    fun setData(data: ArrayList<Uri>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getCount() = mData.size

    private var mOnViewPagerClickListener: OnViewPagerClickListener? = null

    fun setListener(listener: OnViewPagerClickListener) {
        mOnViewPagerClickListener = listener
    }

    interface OnViewPagerClickListener {
        fun onViewPagerClick()
    }
}