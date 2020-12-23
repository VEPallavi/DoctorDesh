package com.doctordesh.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.models.Option

class OptionsAdapter(var mContext: Context, var optionsList: List<Option>, var optionSelectListener:OnOptionSelectListener) :
    RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {

        return OptionsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_question_option,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        holder.bind(optionsList.get(position))
    }


    inner class OptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var cbOption: AppCompatRadioButton

        init {
            cbOption = view.findViewById(R.id.cb_option)
        }


        fun bind(option: Option) {
            cbOption.setText(option.option+" ("+option.option_marks+")")
            cbOption.isChecked = option.isChecked
            cbOption.setOnClickListener {
                for (i in optionsList.indices) {
                    optionsList[i].isChecked = false
                }
                optionsList[adapterPosition].isChecked = true
                optionSelectListener.onOptionSelect(adapterPosition)
                notifyDataSetChanged()
            }

        }

    }


}
