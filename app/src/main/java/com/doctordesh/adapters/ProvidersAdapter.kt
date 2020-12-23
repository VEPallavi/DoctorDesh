package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.activity.MessageProviderActivity
import com.doctordesh.models.ProviderModel
import com.google.gson.Gson
import android.net.Uri
import com.doctordesh.activity.VideoCallInfoActivity


class ProvidersAdapter(var mContext: Context, var providerType: Int, var providersList: List<ProviderModel>) :
    RecyclerView.Adapter<ProvidersAdapter.ProvidersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvidersViewHolder {
        return ProvidersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_providers_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return providersList.size
    }

    override fun onBindViewHolder(holder: ProvidersViewHolder, position: Int) {
        holder.bind(position)

    }

    inner class ProvidersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var clMain: ConstraintLayout
        var videoDialog: Dialog? = null
        var profileImage: ImageView? = null
        var tvName: TextView? = null
        var tvCategory: TextView? = null

        init {

            clMain = view.findViewById(R.id.cl_main)
            tvName = view.findViewById(R.id.tv_name)
            tvCategory = view.findViewById(R.id.tv_category)

            profileImage = view.findViewById<ImageView>(R.id.profile_image)
            itemView.setOnClickListener(View.OnClickListener {
                if (providerType == 2) {
                    mContext.startActivity(Intent(mContext, MessageProviderActivity::class.java).putExtra("provider", Gson().toJson(providersList.get(position))))
                } else if (providerType == 3) {
                    openVideoDialog()
                } else {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + providersList.get(position).phoneNumber)
                    mContext.startActivity(intent)
                }
            })


        }


        fun openVideoDialog() {

            if (videoDialog != null && videoDialog!!.isShowing) {
                videoDialog!!.dismiss()
            }


            videoDialog = Dialog(mContext)
            videoDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            videoDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            videoDialog!!.setContentView(R.layout.dialog_video_call)
            videoDialog!!.show()
            videoDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


            var tvVideoCall = videoDialog!!.findViewById<TextView>(R.id.tv_video_call)
            tvVideoCall.setOnClickListener(View.OnClickListener {

                val intent = Intent(mContext, VideoCallInfoActivity::class.java)
                //intent.putExtra("from", "providersScreen")
               // intent.putExtra("provider", "" + Gson().toJson(providersList.get(position)).toString())
                mContext.startActivity(intent)
                videoDialog!!.dismiss()
            })
            var tvCall = videoDialog!!.findViewById<TextView>(R.id.tv_call)
            tvCall.setOnClickListener(View.OnClickListener {
                videoDialog!!.dismiss()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + providersList.get(position).phoneNumber)
                mContext.startActivity(intent)


            })


            var tvMessage = videoDialog!!.findViewById<TextView>(R.id.tv_message)
            tvMessage.setOnClickListener(View.OnClickListener {
                mContext.startActivity(
                    Intent(mContext, MessageProviderActivity::class.java).putExtra(
                        "provider",
                        Gson().toJson(providersList.get(position))
                    )
                )
                videoDialog!!.dismiss()
            })


        }

        fun bind(position: Int) {

            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(mContext)
                .load(providersList.get(position).profilePic)
                .apply(options)
                .into(profileImage!!)


            tvName!!.text = providersList.get(position).firstName + " " + providersList.get(position).lastName
            tvCategory!!.text = providersList.get(position).specialist + " • " + providersList.get(position).phoneNumber


        }


    }
}