package com.greentoad.turtlebody.imagepreview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/*********************
 *    ViewPager
 **********************/
class ImageViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    private val TAG = "ImageViewPager"

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "Error: " + ex.localizedMessage)
        }
        return false
    }

}