package com.doctordesh.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.models.ImageChatModel

class ImageChatMessageAdapter(
    var mContext: Context,
    var imagesList: List<ImageChatModel>,
    var onDelete: OnDeleteImage
) :
    RecyclerView.Adapter<ImageChatMessageAdapter.ImageChatMessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageChatMessageViewHolder {
        return ImageChatMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image_chat_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ImageChatMessageViewHolder, position: Int) {
        holder.bind(imagesList.get(position))

    }

    inner class ImageChatMessageViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var ivImage: ImageView
        var ivDelete: ImageView
        var progressUpload: ProgressBar

        init {
            ivImage = view.findViewById(R.id.iv_image)
            ivDelete = view.findViewById(R.id.iv_delete)
            progressUpload = view.findViewById(R.id.progress_upload)
        }

        fun bind(image: ImageChatModel) {

            Glide.with(mContext)
                .load(image.imagePath)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(ivImage)

            ivDelete.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    onDelete.onDelete(position)

                }

            })

            if (image.isUploading) {
                progressUpload.visibility = View.VISIBLE
            } else {
                progressUpload.visibility = View.GONE
            }


        }

    }

    interface OnDeleteImage {
        fun onDelete(position: Int)

    }


}