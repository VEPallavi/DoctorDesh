package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.SurveyAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.SurveyModel
import com.doctordesh.network.ApiList
import com.doctordesh.viewModels.SurveyListViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


import kotlinx.android.synthetic.main.activity_survey_list.*
import kotlinx.android.synthetic.main.activity_survey_list.toolbar

class SurveyListActivity : AppCompatActivity() {


    var surveyListViewModel: SurveyListViewModel? = null
    var from: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_list)
        initView()


    }

    fun initView() {


        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }


        var URL = ""
        if (intent.getStringExtra("from").equals("STAFF")) {
            URL = ApiList.GET_SURVEYS_FROM_STAFF_URL

        } else if (intent.getStringExtra("from").equals("PROVIDER")) {
            URL = ApiList.GET_PROVIDER_SURVEY_URL

        } else {
            URL = ApiList.GET_SURVEY_URL
        }


        surveyListViewModel = ViewModelProviders.of(this).get(SurveyListViewModel::class.java)


        surveyListViewModel!!.getSurveyList(this, URL).observe(this, Observer {


            if (it != null) {


                if (it.has("status") && it.get("status").asString.equals("1") && it.has("serveys")) {


                    val type = object : TypeToken<ArrayList<SurveyModel>>() {}.type


                    var surveysList =
                        Gson().fromJson<ArrayList<SurveyModel>>(it.get("serveys").toString(), type)


                    if (surveysList != null && surveysList.size > 0) {
                        tv_no_survey.visibility = View.GONE

                        rv_surveys.setHasFixedSize(true)
                        rv_surveys.layoutManager = LinearLayoutManager(this)
                        rv_surveys.adapter = SurveyAdapter(this, surveysList)


                    } else {
                        tv_no_survey.visibility = View.VISIBLE


                    }


                }

                Utils.showLog(it.toString())


            }


        })


    }


}
