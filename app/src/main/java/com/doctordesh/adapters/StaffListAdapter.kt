package com.doctordesh.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.doctordesh.activity.MessageProviderActivity
import com.doctordesh.models.ProviderModel
import com.google.gson.Gson

class StaffListAdapter(
    var mContext: Context,
    var membersList: List<ProviderModel>,
    var memberType: String
) :
    RecyclerView.Adapter<StaffListAdapter.StaffListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {

        return StaffListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_providers_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {
        holder.bind(membersList.get(position))

    }

    inner class StaffListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var clMain: ConstraintLayout
        var profileImage: ImageView? = null
        var tvName: TextView? = null
        var tvCategory: TextView? = null


        init {
            clMain = view.findViewById(R.id.cl_main)
            tvName = view.findViewById(R.id.tv_name)
            tvCategory = view.findViewById(R.id.tv_category)
            profileImage = view.findViewById<ImageView>(R.id.profile_image)

            itemView.setOnClickListener(View.OnClickListener {


                if (memberType == "1") {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + membersList.get(position).phoneNumber)
                    mContext.startActivity(intent)
                } else {
                    mContext.startActivity(
                        Intent(
                            mContext,
                            MessageProviderActivity::class.java
                        ).putExtra("provider", Gson().toJson(membersList.get(position)))
                    )
                }


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
