package com.greentoad.turtlebody.imagepreview.sample


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur
import com.greentoad.turtlebody.imagepreview.ImagePreview
import com.greentoad.turtlebody.imagepreview.core.ImagePreviewConfig
import com.greentoad.turtlebody.mediapicker.MediaPicker
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class ActivityHome : AppCompatActivity(), AnkoLogger {

    private var mImagePreview = ImagePreview.with(this)


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initStatusBar()



        //setSupportActionBar(activity_home_tool_bar)
        initButton()

        Glide.with(this).load(R.drawable.pic_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //blurr()
                    return false
                }

            })
            .into(image_view)

        setUpBlur()

    }



    private fun setUpBlur() {
        val radius = 2f
        val windowBackground = window.decorView.background

        blurView.setupWith(root)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(SupportRenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        blurView3.setupWith(root)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(SupportRenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        blurView2.setupWith(root)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(SupportRenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
    }

    private fun initButton() {
//        activity_home_picker_btn.setOnClickListener {
//            startMediaPicker()
//        }
//
//        activity_home_full_screen_btn.setOnClickListener {
//            startActivity<FullscreenActivity>()
//        }
//
//        activity_home_full_screen_btn2.setOnClickListener {
//            startActivity<TestActivityScreen>()
//        }

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
            .setConfig(
                ImagePreviewConfig().setAllowButton(true)
            )
            .onResult()
    }


    private fun initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.md_white_1000)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

}
