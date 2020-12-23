package com.doctordesh.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.activity.OnQuestionAnswered
import com.doctordesh.models.Option
import com.doctordesh.models.PsychQuestionModel


class QuestionsListAdapter(
    var mContext: Context,
    var psychQuestionsList: List<PsychQuestionModel>, var onQuestionAnswered: OnQuestionAnswered
) : RecyclerView.Adapter<QuestionsListAdapter.QuestionsListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsListViewHolder {
        return QuestionsListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_questions_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return psychQuestionsList.size
    }

    override fun onBindViewHolder(holder: QuestionsListViewHolder, position: Int) {

        holder.bind(psychQuestionsList.get(position))

    }


    inner class QuestionsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvQuestion: TextView

        var rvOptions: RecyclerView

        init {
            tvQuestion = view.findViewById(R.id.tv_question)
            rvOptions = view.findViewById(R.id.rv_options)
            rvOptions.setHasFixedSize(true)
            rvOptions.layoutManager = LinearLayoutManager(mContext)


        }


        fun bind(psychQuestion: PsychQuestionModel) {
            tvQuestion.text = psychQuestion.questions



            rvOptions.adapter = OptionsAdapter(mContext, psychQuestion.options,
                object : OnOptionSelectListener {
                    override fun onOptionSelect(num: Int) {

                        psychQuestionsList[adapterPosition].selectedOption = num
                        onQuestionAnswered.onAnswer(adapterPosition, num)

                    }
                })


        }


    }


}


interface OnOptionSelectListener {
    fun onOptionSelect(num: Int)
}