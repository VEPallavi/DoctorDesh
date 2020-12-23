package com.doctordesh.adapters

import android.content.Context
import android.net.Uri
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.models.PsychMaterialModel
import com.doctordesh.models.SurveyModel

class PsychMaterialAdapter(var mContext: Context, var materialList: List<PsychMaterialModel>) : RecyclerView.Adapter<PsychMaterialAdapter.PsychMaterialViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PsychMaterialViewHolder {
        return PsychMaterialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_survey,parent,false))
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: PsychMaterialViewHolder, position: Int) {
        holder.bind(materialList.get(position))
    }


    inner class PsychMaterialViewHolder(mView : View) : RecyclerView.ViewHolder(mView)
    {

        var tvSurveyTitle: TextView
        var clMainItem: ConstraintLayout


        init {
            tvSurveyTitle=mView.findViewById(R.id.tv_survey_title)
            clMainItem=mView.findViewById(R.id.cl_item)
        }


        fun bind(survey: PsychMaterialModel)
        {

            tvSurveyTitle.text=survey.servey_title


            clMainItem.setOnClickListener {

                var URL:String=""
                if(survey.servey_link!!.contains("http") || survey.servey_link.contains("https"))
                {
                    URL=survey.servey_link

                }
                else
                {
                    URL="http://"+survey.servey_link

                }




                var builder = CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                builder.addDefaultShareMenuItem()
                builder.setShowTitle(true)
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(mContext, Uri.parse(URL))



            }






        }


    }

}