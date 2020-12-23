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

class QuestionsCategoriesAdapter(var mContext: Context, var psychCategoriesList: List<PsychCategoryModel>,var onCategorySelectListener:OnCategorySelectListener) :
    RecyclerView.Adapter<QuestionsCategoriesAdapter.QuestionsCategoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsCategoriesViewHolder {
        return QuestionsCategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_question_category,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return psychCategoriesList.size
    }

    override fun onBindViewHolder(holder: QuestionsCategoriesViewHolder, position: Int) {

        holder.bind(psychCategoriesList.get(position))
    }

    inner class QuestionsCategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var tvAssesmentTitle: TextView


        init {
            tvAssesmentTitle = view.findViewById(R.id.tv_assesment_title)

            view.setOnClickListener(View.OnClickListener {


                onCategorySelectListener.onCategorySelect(psychCategoriesList[adapterPosition])


            })
        }


        fun bind(category: PsychCategoryModel) {

            tvAssesmentTitle.text = category.category_name

        }


    }




}


interface OnCategorySelectListener
{
    fun onCategorySelect(selectedCategory:PsychCategoryModel)

}