package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import android.view.Gravity
import android.view.View
import com.doctordesh.R
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_education_material.*

class EducationMaterialActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_education_material)

        initView()

    }

    fun initView() {
        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        cv_education_videos.setOnClickListener(this)
        cv_psych_meds.setOnClickListener(this)
        cv_psych_disorders.setOnClickListener(this)



        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_education_videos.visibility = View.VISIBLE

        }, 500)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_psych_meds.visibility = View.VISIBLE

        }, 600)
        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_psych_disorders.visibility = View.VISIBLE

        }, 700)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cv_education_videos -> startActivity(
                Intent(
                    this,
                    EducationalVideosActivity::class.java
                )
            )
            R.id.cv_psych_meds -> startActivity(
                Intent(
                    this,
                    PsychMaterialActivity::class.java
                ).putExtra("from", getString(R.string.text_learn_psych_meds))
            )
            R.id.cv_psych_disorders -> startActivity(
                Intent(
                    this,
                    PsychMaterialActivity::class.java
                ).putExtra("from", getString(R.string.text_learn_psych_disorders))
            )
        }
    }

}
