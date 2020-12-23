package com.doctordesh.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.models.ProviderTimingModel

class ProviderTimingAdapter(var mContext: Context, var timingList: List<ProviderTimingModel>) :
    RecyclerView.Adapter<ProviderTimingAdapter.ProviderTimingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderTimingViewHolder {
        return ProviderTimingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_timing,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return timingList.size
    }

    override fun onBindViewHolder(holder: ProviderTimingViewHolder, position: Int) {
        holder.bind(timingList.get(position))
    }

    inner class ProviderTimingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvDay: TextView
        var tvTiming: TextView
        var clMain: ConstraintLayout


        init {
            tvDay = view.findViewById(R.id.tv_day)
            tvTiming = view.findViewById(R.id.tv_timing)
            clMain = view.findViewById(R.id.cl_main)


        }

        fun bind(providerTiming: ProviderTimingModel) {


            if(adapterPosition%2==0)
            {
                clMain.setBackgroundColor(mContext.resources.getColor(R.color.colorWhite))
            }
            else
            {
                clMain.setBackgroundColor(mContext.resources.getColor(R.color.colorBlack_1))
            }

            tvDay.text = providerTiming.day
            tvTiming.text = providerTiming.start_time!!.toUpperCase() + " - " + providerTiming.end_time!!.toUpperCase()

        }
    }
}