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
import kotlinx.android.synthetic.main.activity_educational_videos.*

class EducationalVideosActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_educational_videos)

        initView()

    }

    fun initView()
    {

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }



        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_education_videos_team.visibility = View.VISIBLE

        }, 500)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_general_videos.visibility = View.VISIBLE

        }, 600)

        cv_general_videos.setOnClickListener(this)
        cv_education_videos_team.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.cv_general_videos -> startActivity(Intent(this,EducationalVideoActivity::class.java).putExtra("from","GENERAL"))

            R.id.cv_education_videos_team -> startActivity(Intent(this,EducationalVideoActivity::class.java).putExtra("from","PROVIDERS"))


        }
    }
}
