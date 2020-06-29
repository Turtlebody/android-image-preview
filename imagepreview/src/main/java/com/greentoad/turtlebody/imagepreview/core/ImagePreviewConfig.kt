package com.greentoad.turtlebody.imagepreview.core

import android.net.Uri
import java.io.Serializable

/**
 * Created by WANGSUN on 08-May-19.
 */
class ImagePreviewConfig: Serializable {
    var mAllowAddButton: Boolean = false
    var mDisplayThumbnailLowerBar: Boolean = true
        private set // the setter is private and has the default implementation

    companion object{
        val ARG_BUNDLE = ImagePreviewConfig::class.java.name + ".bundle_arg"
    }

    var mUriList: ArrayList<String> = arrayListOf()
        private set

    /**
     * @param value: false to hide add button, true to show add button
     */
    fun setAllowAddButton(value: Boolean): ImagePreviewConfig{
        mAllowAddButton = value
        return this
    }

    /**
     * @param value: false to hide add button, true to show add button
     */
    fun setDisplayThumbnailLowerBar(value: Boolean): ImagePreviewConfig{
        mDisplayThumbnailLowerBar = value
        return this
    }

    /**
     * @param value: array of uri to be sent for preview
     */
    fun setUris(value: ArrayList<Uri>): ImagePreviewConfig{
        var uris: ArrayList<String> = arrayListOf()
        for(uri in value){
            uris.add(uri.toString())
        }
        mUriList = uris
        return this
    }

    /**
     * @param value: array of uri to be sent for preview
     */
    fun getUris(): ArrayList<Uri>{
        var uris: ArrayList<Uri> = arrayListOf()
        for(uri in mUriList){
            uris.add(Uri.parse(uri))
        }
        return uris
    }
}