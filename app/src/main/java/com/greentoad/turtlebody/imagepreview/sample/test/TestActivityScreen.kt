package com.greentoad.turtlebody.imagepreview.sample.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.greentoad.turtlebody.imagepreview.sample.R
import kotlinx.android.synthetic.main.activity_test_screen.*

class TestActivityScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_screen)

        //setSupportActionBar(test_activity_screen_tool_bar)

        statusBar()

        show()

        btn_hide.setOnClickListener {
            hide()
        }


        btn_show.setOnClickListener {
            show()
            statusBar()
        }



    }

    fun show(){
        fullscreen_content2.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        test_activity_screen_app_bar.visibility = View.VISIBLE
    }

    fun hide(){
        fullscreen_content2.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        test_activity_screen_app_bar.visibility = View.GONE
    }

    fun statusBar(){
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}
