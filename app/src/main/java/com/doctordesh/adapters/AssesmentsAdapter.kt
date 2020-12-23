package com.doctordesh.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.activity.PsychometricQuestionsActivity
import com.doctordesh.models.PsychCategoryModel
import java.util.ArrayList

class AssesmentsAdapter(var mContext: Context, var psychCategoriesList: List<PsychCategoryModel>) :
    RecyclerView.Adapter<AssesmentsAdapter.AssesmentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssesmentsViewHolder {
        return AssesmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_assesments,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return psychCategoriesList.size
    }

    override fun onBindViewHolder(holder: AssesmentsViewHolder, position: Int) {

        holder.bind(psychCategoriesList.get(position))
    }

    inner class AssesmentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var tvAssesmentTitle: TextView


        init {
            tvAssesmentTitle = view.findViewById(R.id.tv_assesment_title)

            view.setOnClickListener(View.OnClickListener {


                var intent = Intent(mContext, PsychometricQuestionsActivity::class.java)
                intent.putExtra("psychCategory", psychCategoriesList.get(adapterPosition))

                var bundle = Bundle()
                bundle.putParcelableArrayList(
                    "questionsCategoriesList",
                    psychCategoriesList as ArrayList<PsychCategoryModel>
                )
                intent.putExtra("question", bundle)
                mContext.startActivity(intent)


            })
        }


        fun bind(category: PsychCategoryModel) {

            tvAssesmentTitle.text = category.category_name

        }


    }
}