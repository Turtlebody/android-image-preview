package com.greentoad.turtlebody.imagepreview.sample

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.WindowManager.*
import android.widget.Toast
import com.greentoad.turtlebody.imagepreview.ImagePreview
import com.greentoad.turtlebody.mediapicker.MediaPicker
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ActivityHome : AppCompatActivity(),AnkoLogger {

    private var mImagePreview  = ImagePreview.with(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.setShowHideAnimationEnabled(true)
        initButton()
    }

    @SuppressLint("CheckResult")
    private fun initButton() {
        activity_home_picker_btn.setOnClickListener {
            MediaPicker.with(this, MediaPicker.MediaTypes.IMAGE)
                .setConfig(
                    MediaPickerConfig()
                        .setUriPermanentAccess(false)
                        .setAllowMultiSelection(true)
                        .setShowConfirmationDialog(true))
                .setFileMissingListener(object : MediaPicker.MediaPickerImpl.OnMediaListener{
                    override fun onMissingFileWarning() {
                        Toast.makeText(this@ActivityHome,"some file is missing",Toast.LENGTH_LONG).show()
                    }
                })
                .onResult()
                .subscribe({
                    info { "success: $it" }
                    info { "list size: ${it.size}" }
                    startActivityLibMain(it)
                },{
                    info { "error: $it" }
                })
        }

    }

    private fun startActivityLibMain(it: ArrayList<Uri>) {
        mImagePreview.setUris(it)
            .onResult()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mImagePreview.setOriginalState()
    }
}
