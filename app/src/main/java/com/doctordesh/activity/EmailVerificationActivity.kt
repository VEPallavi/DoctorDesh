package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.EmailVerificationModel
import kotlinx.android.synthetic.main.activity_email_verification.*

class EmailVerificationActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_verify -> {

                if (checkOTPValidation()) {
                    if (from == 2) {
                        startActivity(
                            Intent(this@EmailVerificationActivity, SavePasswordActivity::class.java).putExtra(
                                "email",
                                et_email_id.text.toString()
                            )
                        )
                        finish()
                    } else {
                        startActivity(Intent(this@EmailVerificationActivity, DashboardActivity::class.java))
                        finish()
                    }
                }


            }
            R.id.tv_send_code -> {

                if (checkValidation()) {
                    Utils.hideSoftKeyboard(this@EmailVerificationActivity)
                    /* Handler().postDelayed(Runnable {

                         var transition = Slide(Gravity.BOTTOM)
                         transition.setDuration(500)
                         TransitionManager.beginDelayedTransition(cl_verification_main, transition)
                         cl_verify_otp.visibility = View.VISIBLE

                     }, 600)
 */

                    emailVerificationModel!!.verifyEmail(et_email_id.text.toString(), this).observe(this, Observer {

                        Utils.showLog(it.toString())
                        if (it.has("status") && it.get("status").asString.equals("1")) {

                            if (it.has("otp")) {
                                OTP = it.get("otp").asString

                                Handler().postDelayed(Runnable {

                                    var transition = Slide(Gravity.BOTTOM)
                                    transition.setDuration(500)
                                    TransitionManager.beginDelayedTransition(cl_verification_main, transition)
                                    cl_verify_otp.visibility = View.VISIBLE

                                }, 600)

                            } else {
                                Utils.showToast(this, resources.getString(R.string.msg_common_error))
                            }


                        } else {
                            if (it.has("message"))
                                Utils.showToast(this, it.get("message").asString)
                        }

                    })


                }


            }
        }
    }


    var OTP = ""

    var from = 1;
    ///////////////////////
    ///// FROM SCREEN /////
    // 1. SIGNUP SCREEN
    // 2. FORGOT PASSWORD SCREEN
    var emailVerificationModel: EmailVerificationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)
        initView()
    }


    private fun checkValidation(): Boolean {

        if (!Utils.isInternetAvailable(this)) {
            Utils.showToast(this, resources.getString(R.string.msg_no_internet))
            return false
        } else if (!Utils.isEmailValid(et_email_id.text.toString())) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_email))
            return false
        }
        return true

    }

    private fun checkOTPValidation(): Boolean {
        if (et_code_1.text!!.length == 0 || et_code_2.text!!.length == 0 || et_code_3.text!!.length == 0 || et_code_4.text!!.length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_otp))
            return false
        } else if (!OTP.equals(et_code_1.text!!.toString() + et_code_2.text!!.toString() + et_code_3.text!!.toString() + et_code_4.text!!.toString())) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_otp))
            return false
        }


        return true
    }


    private fun initView() {


        emailVerificationModel = ViewModelProviders.of(this).get(EmailVerificationModel::class.java)


        if (intent.hasExtra("from"))
            from = intent.getIntExtra("from", 1)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        btn_verify.setOnClickListener(this)
        tv_send_code.setOnClickListener(this)


    }
}
