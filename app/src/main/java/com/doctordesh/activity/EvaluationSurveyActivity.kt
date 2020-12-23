package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.doctordesh.R
import kotlinx.android.synthetic.main.activity_evaluation_survey.*

class EvaluationSurveyActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation_survey)

        initView()

    }

    fun initView() {
        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        cv_provider.setOnClickListener(this)
        cv_staff.setOnClickListener(this)
        cv_app_survey.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cv_provider -> {
                startActivity(Intent(this, SurveyListActivity::class.java).putExtra("from","PROVIDER"))
            }

            R.id.cv_staff -> {
                startActivity(Intent(this, SurveyListActivity::class.java).putExtra("from","STAFF"))
            }

            R.id.cv_app_survey -> {
                startActivity(Intent(this, SurveyListActivity::class.java).putExtra("from","APP"))
            }
        }
    }


}
