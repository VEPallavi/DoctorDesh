package com.doctordesh.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.activity.WatchVideosActivity
import com.doctordesh.models.VideosModel
import com.google.gson.Gson

class VideosAdapter(var mContext: Context, var videosList: List<VideosModel>) :
    RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {
    override fun getItemCount(): Int {
        return videosList.size
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {

        holder.bind(videosList.get(position))

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        return VideosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_videos_list, parent, false))
    }

    inner class VideosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivVideoImage: ImageView
        var tvVideoTitle: TextView

        init {
            ivVideoImage = itemView.findViewById(R.id.iv_video_image)
            tvVideoTitle = itemView.findViewById(R.id.tv_video_title)

        }

        fun bind(videos: VideosModel) {
            tvVideoTitle.text = videos.video_title
            Glide.with(mContext)
                .load(videos!!.video_thumbnail)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(ivVideoImage)

            itemView.setOnClickListener(View.OnClickListener {


                var video = Gson().toJson(videosList.get(position))

                mContext.startActivity(
                    Intent(
                        mContext,
                        WatchVideosActivity::class.java
                    ).putExtra("video", video)
                )
            })


        }


    }
}