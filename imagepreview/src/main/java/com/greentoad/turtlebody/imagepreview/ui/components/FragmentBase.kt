package com.greentoad.turtlebody.imagepreview.ui.components

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.lang.reflect.InvocationTargetException

/**
 * Created by WANGSUN on 08-May-19.
 */
abstract class FragmentBase : DialogFragment(), AnkoLogger {


    @Deprecated("return value in physical-bottom-navigational devices")
    private fun getBottomNavigationalBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            info { "bottomHeight: ${resources.getDimensionPixelSize(resourceId)}" }
            resources.getDimensionPixelSize(resourceId)
        } else {
            info { "bottomHeight: 0" }
            0
        }
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getNavigationBarSize(context: Context): Point {
        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else
            Point()
        // navigation bar is not present
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
        } else { //if (Build.VERSION.SDK_INT >= 14)
            try {
                size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as Int
                size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as Int
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            } catch (e: NoSuchMethodException) {
            }
        }
        return size
    }
}