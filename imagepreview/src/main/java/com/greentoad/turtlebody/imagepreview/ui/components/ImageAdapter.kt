package com.greentoad.turtlebody.imagepreview.ui.components

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greentoad.turtlebody.imagepreview.R
import kotlinx.android.synthetic.main.tb_image_preview_item_image.view.*

/**
 * Created by WANGSUN on 02-May-19.
 */
class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ImageVewHolder>(){

    private var mData: ArrayList<Uri> = arrayListOf()
    private var mCurrentSelectedIndex = -1
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tb_image_preview_item_image, parent, false)
        return ImageVewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ImageAdapter.ImageVewHolder, position: Int) {
        holder.bind(mData[position],position)
    }

    fun setData(data: ArrayList<Uri>){
        if(data.isNotEmpty())
            mCurrentSelectedIndex=0
        mData = data
        notifyDataSetChanged()
    }

    fun selectPosition(index: Int) {
        if (mCurrentSelectedIndex >= 0) {
            val prevIndex = mCurrentSelectedIndex
            mCurrentSelectedIndex = index
            notifyItemChanged(prevIndex)
            notifyItemChanged(index)
        } else {
            mCurrentSelectedIndex = index
            notifyItemChanged(index)
        }
    }

    fun getSelectedItem(): Int {
        return mCurrentSelectedIndex
    }

    inner class ImageVewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(pData: Uri,position: Int){
            Glide.with(itemView)
                .load(pData)
                .into(itemView.item_image_iv)

            itemView.setOnClickListener {
                mOnRecyclerImageClickListener?.onRecyclerImageClick(position)
                selectPosition(position)
            }

            if (mCurrentSelectedIndex == position)
                setSelected(true)
            else
                setSelected(false)
        }

        private fun setSelected(isSelected: Boolean) {
            if (isSelected)
                itemView.item_image_iv.setBackgroundColor(ContextCompat.getColor(mContext,R.color.tb_image_preview_accent))
            else
                itemView.item_image_iv.setBackgroundColor(ContextCompat.getColor(mContext,R.color.md_white_1000))
        }
    }




    private var mOnRecyclerImageClickListener: OnRecyclerImageClickListener? = null

    fun setListener(listener: OnRecyclerImageClickListener) {
        mOnRecyclerImageClickListener = listener
    }

    interface OnRecyclerImageClickListener {
        fun onRecyclerImageClick(index: Int)
    }
}