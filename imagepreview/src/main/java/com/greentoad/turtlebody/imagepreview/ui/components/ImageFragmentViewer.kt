package com.greentoad.turtlebody.imagepreview.ui.components


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.greentoad.turtlebody.imagepreview.R
import com.greentoad.turtlebody.imagepreview.ui.components.ViewPagerAdapter.Companion.B_ARG_URI
import kotlinx.android.synthetic.main.tb_image_preview_view_pager.*
import org.jetbrains.anko.AnkoLogger


class ImageFragmentViewer : Fragment(), AnkoLogger {

    private lateinit var mUri: Uri

    companion object {
        @JvmStatic
        fun newInstance(key: Int, b: Bundle?): ImageFragmentViewer {
            val bf: Bundle = b ?: Bundle()
            bf.putInt("fragment.key", key)
            val fragment = ImageFragmentViewer()
            fragment.arguments = bf
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = requireArguments()
        mUri = Uri.parse(b.getString(B_ARG_URI, ""))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tb_image_preview_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this).asBitmap().load(mUri).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object :
                CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view_pager_iv.setImage(ImageSource.bitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        view_pager_iv.setOnClickListener {
            mOnImageClickListener?.onImageClick()
        }
    }

    private var mOnImageClickListener: OnImageClickListener? = null

    fun setListener(listener: OnImageClickListener) {
        mOnImageClickListener = listener
    }

    interface OnImageClickListener {
        fun onImageClick()
    }

}
