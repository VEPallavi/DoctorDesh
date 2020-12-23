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
import kotlinx.android.synthetic.main.activity_staff_member.*

class StaffMemberActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_member)
        initView()
    }

    fun initView() {


        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }


        cv_staff_call_provider.setOnClickListener(this)
        cv_staff_message_provider.setOnClickListener(this)



        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_staff_call_provider.visibility = View.VISIBLE

        }, 300)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_staff_message_provider.visibility = View.VISIBLE

        }, 500)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.cv_staff_call_provider -> {
                startActivity(
                    Intent(
                        this,
                        StaffMembersListActivity::class.java
                    ).putExtra("provider_type", "1")
                )
            }

            R.id.cv_staff_message_provider -> {
                startActivity(
                    Intent(
                        this,
                        StaffMembersListActivity::class.java
                    ).putExtra("provider_type", "2")
                )
            }


        }
    }


}
