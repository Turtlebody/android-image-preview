package com.greentoad.turtlebody.imagepreview.core

import android.view.View
import java.io.Serializable

/**
 * Created by WANGSUN on 08-May-19.
 */
class ImagePreviewConfig: Serializable {
    var mAllowAddButton: Boolean = false
    private set // the setter is private and has the default implementation

    fun setAllowButton(value: Boolean): ImagePreviewConfig{
        mAllowAddButton = value
        return this
    }
}