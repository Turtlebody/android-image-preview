package com.greentoad.turtlebody.imagepreview.sample

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.WindowManager
import android.view.WindowManager.*
import android.widget.Toast
import com.greentoad.turtlebody.imagepreview.ImagePreview
import com.greentoad.turtlebody.imagepreview.sample.test.FullscreenActivity
import com.greentoad.turtlebody.mediapicker.MediaPicker
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import android.view.ViewConfiguration
import com.greentoad.turtlebody.imagepreview.sample.test.TestActivityScreen
import com.greentoad.turtlebody.imagepreview.utils.UtilFunction.hasSoftKeys


class ActivityHome : AppCompatActivity(),AnkoLogger {

    private var mImagePreview  = ImagePreview.with(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setSupportActionBar(activity_home_tool_bar)
        initButton()

        info { "softKey: ${hasSoftKeys(this)}" }
    }


    private fun initButton() {
        activity_home_picker_btn.setOnClickListener {
            startMediaPicker()
        }

        activity_home_full_screen_btn.setOnClickListener {
            startActivity<FullscreenActivity>()
        }

        activity_home_full_screen_btn2.setOnClickListener {
            startActivity<TestActivityScreen>()
        }

    }

    @SuppressLint("CheckResult")
    private fun startMediaPicker() {
        MediaPicker.with(this, MediaPicker.MediaTypes.IMAGE)
            .setConfig(
                MediaPickerConfig()
                    .setUriPermanentAccess(false)
                    .setAllowMultiSelection(true)
                    .setShowConfirmationDialog(true)
            )
            .setFileMissingListener(object : MediaPicker.MediaPickerImpl.OnMediaListener {
                override fun onMissingFileWarning() {
                    Toast.makeText(this@ActivityHome, "some file is missing", Toast.LENGTH_LONG).show()
                }
            })
            .onResult()
            .subscribe({
                info { "success: $it" }
                info { "list size: ${it.size}" }
                startActivityLibMain(it)
            }, {
                info { "error: $it" }
            })
    }

    private fun startActivityLibMain(it: ArrayList<Uri>) {
        mImagePreview.setUris(it)
            .onResult()
    }

    override fun onBackPressed() {
        super.onBackPressed()
       // mImagePreview.setOriginalState()
    }

}
