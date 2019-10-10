package com.greentoad.turtlebody.imagepreview.core

import android.net.Uri
import java.io.Serializable

/**
 * Created by WANGSUN on 08-May-19.
 */
class ImagePreviewConfig : Serializable {
    var mAllowAddButton: Boolean = false
        private set // the setter is private and has the default implementation

    companion object {
        val ARG_BUNDLE = javaClass.canonicalName + ".bundle_arg"
    }

    var mUriList: ArrayList<Uri> = arrayListOf()
        private set

    /**
     * @param value: false to hide add button, true to show add button
     */
    fun setAllowAddButton(value: Boolean): ImagePreviewConfig {
        mAllowAddButton = value
        return this
    }

    /**
     * @param value: array of uri to be sent for preview
     */
    fun setUris(value: ArrayList<Uri>): ImagePreviewConfig {
        mUriList = value
        return this
    }
}