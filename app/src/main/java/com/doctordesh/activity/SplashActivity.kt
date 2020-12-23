package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.doctordesh.R
import com.doctordesh.helpers.AppPreference
import com.doctordesh.models.UserModel
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_sign_in -> {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
            R.id.tv_sign_up -> {
                val intent = Intent(this@SplashActivity, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this)
            .load(resources.getDrawable(R.drawable.spalsh))
            .into(iv_main)


        Handler().postDelayed(Runnable {


            if (AppPreference.getInstance(this).getUserData() != null && !AppPreference.getInstance(this).getUserData()!!.equals("")) {

                var user=AppPreference.getInstance(this).getUserData() as UserModel

                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                finish()

            } else {
                var transition = Slide(Gravity.BOTTOM)
                transition.setDuration(500)
                TransitionManager.beginDelayedTransition(cl_splash_main, transition)
                cl_bottom.visibility = View.VISIBLE

            }

        }, 2000)

        tv_sign_up.setOnClickListener(this)

        tv_sign_in.setOnClickListener(this)


    }
}
