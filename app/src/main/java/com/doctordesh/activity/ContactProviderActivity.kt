package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.doctordesh.R
import kotlinx.android.synthetic.main.activity_contact_provider.*

class ContactProviderActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.cv_call_provider -> {
                startActivity(
                    Intent(
                        this@ContactProviderActivity,
                        ProvidersListActivity::class.java
                    ).putExtra("provider_type", 1)
                )
            }
            R.id.cv_message_provider -> {
                startActivity(
                    Intent(
                        this@ContactProviderActivity,
                        ProvidersListActivity::class.java
                    ).putExtra("provider_type", 2)
                )
            }
            R.id.cv_videos_provider -> {
                startActivity(
                    Intent(
                        this@ContactProviderActivity,
                        WebViewActivity::class.java
                    ).putExtra("from", "video_call")
                )





            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_provider)
        initView()
    }

    private fun initView() {

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }


        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_call_provider.visibility = View.VISIBLE

        }, 500)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_message_provider.visibility = View.VISIBLE

        }, 600)
        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_videos_provider.visibility = View.VISIBLE

        }, 700)





        cv_call_provider.setOnClickListener(this)
        cv_message_provider.setOnClickListener(this)
        cv_videos_provider.setOnClickListener(this)
    }

}
