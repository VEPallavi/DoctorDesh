package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.SurveyAdapter
import com.doctordesh.models.SurveyModel
import com.doctordesh.viewModels.MeditationWellnessViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_meditation_wellness.*

class MeditationWellnessActivity : AppCompatActivity() {

    var meditationWellnessViewModel: MeditationWellnessViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_wellness)
        initView()
    }

    fun initView() {

        meditationWellnessViewModel =
            ViewModelProviders.of(this).get(MeditationWellnessViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }


        meditationWellnessViewModel!!.getData(this).observe(this, Observer {


            if (it != null && it.has("status") && it.get("status").asString.equals("1")) {

                val type = object : TypeToken<ArrayList<SurveyModel>>() {}.type


                var surveysList =
                    Gson().fromJson<ArrayList<SurveyModel>>(it.get("serveys").toString(), type)

                if (surveysList != null && surveysList.size > 0)
                {
                    rv_meditations_data_list.setHasFixedSize(true)
                    rv_meditations_data_list.layoutManager=LinearLayoutManager(this)
                    rv_meditations_data_list.adapter=SurveyAdapter(this,surveysList)




                }
                else
                {

                }



            }


        })



    }

}
