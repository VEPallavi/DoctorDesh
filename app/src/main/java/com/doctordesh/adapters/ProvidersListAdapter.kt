package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.activity.ProviderScheduleActivty
import com.doctordesh.models.ProviderModel

class ProvidersListAdapter(var mContext: Context, var providersList: List<ProviderModel>) :
    RecyclerView.Adapter<ProvidersListAdapter.ProvidersListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvidersListViewHolder {


        return ProvidersListViewHolder(
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

    override fun onBindViewHolder(holder: ProvidersListViewHolder, position: Int) {
        holder.bind(providersList.get(position))
    }


    inner class ProvidersListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var clMain: ConstraintLayout
        var profileImage: ImageView? = null
        var tvName: TextView? = null
        var tvCategory: TextView? = null

        init {

            clMain = view.findViewById(R.id.cl_main)
            tvName = view.findViewById(R.id.tv_name)
            tvCategory = view.findViewById(R.id.tv_category)
            profileImage = view.findViewById<ImageView>(R.id.profile_image)


            itemView.setOnClickListener({

                mContext.startActivity(
                    Intent(
                        mContext,
                        ProviderScheduleActivty::class.java
                    ).putExtra("provider", providersList.get(position))
                )
            })
        }


        fun bind(provider: ProviderModel) {

            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(mContext)
                .load(provider.profilePic)
                .apply(options)
                .into(profileImage!!)


            tvName!!.text =
                provider.firstName + " " + provider.lastName
            tvCategory!!.text =
                provider.specialist + " • " + provider.phoneNumber


        }

    }


}