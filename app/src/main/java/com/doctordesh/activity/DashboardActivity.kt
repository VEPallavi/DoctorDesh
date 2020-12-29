package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.doctordesh.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.doctordesh.helpers.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import androidx.transition.TransitionManager
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Slide
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.helpers.AppPreference
import com.doctordesh.models.UserModel
import com.doctordesh.viewModels.DashBoardViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class DashboardActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_profile_pic -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.cv_req_med_refill -> {
                startActivity(Intent(this, MedRefillActivity::class.java))
            }
            R.id.cv_contact_provider -> {
                startActivity(Intent(this, ProvidersDashboardActivity::class.java))
            }
            R.id.cv_patient_referral -> {

                openPatientReferralDialog()


            }
            R.id.cv_review_provider_schedule -> {

                startActivity(Intent(this, ProvidersScheduleListingActivity::class.java))
            }
            R.id.cv_patient_consent -> {
                startActivity(Intent(this, PatientConsentActivity::class.java))
            }
            R.id.cv_education_videos -> {
                startActivity(Intent(this, EducationMaterialActivity::class.java))
            }
            R.id.iv_notification -> {
                startActivity(Intent(this, NotificationListActivity::class.java))
            }
            R.id.iv_msg -> {
                startActivity(Intent(this, ConversationActivity::class.java))
            }
            R.id.cv_satisfaction_survey -> {


                startActivity(Intent(this, EvaluationSurveyActivity::class.java))
            }
            R.id.cv_psychometric -> {

                startActivity(Intent(this, PsychometricScaleActivity::class.java))

            }
            R.id.cv_patient_document -> {

                startActivity(Intent(this, SendPatientDocumentActivity::class.java))

            }
            R.id.cv_meditation_survey -> {
                startActivity(Intent(this, MeditationWellnessActivity::class.java))
            }
            R.id.cv_employee_receipt ->{
                startActivity(Intent(this, EmployeeReceiptsActivity::class.java))
            }
            R.id.cv_contact_user ->{
                startActivity(Intent(this, ContactUserActivity::class.java))
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.doctordesh.R.layout.activity_dashboard)
        initView()
        FirebaseApp.initializeApp(applicationContext)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Utils.showLog(task.exception!!.message!!)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                if (token != null) {
                    sendTokenToServer(token)
                }
            })

    }


    override fun onResume() {
        super.onResume()

        preference = AppPreference.getInstance(this)
        user = preference!!.getUserData()

        Utils.showLog("Auth Token : "+preference!!.getAuthToken())


        if (user!!.profilePic != null && !user!!.profilePic.equals("")) {

            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(this)
                .load(user!!.profilePic)
                .apply(options)
                .into(iv_profile_pic)
        }

    }


    private fun getCredentials() {


        dashBoardViewModel!!.getCredentials(this).observe(this, Observer {


            if (it != null) {
                if (it.has("status") && it.get("status").asString.equals("1")) {

                    if (it.has("payload") && it.get("payload") is JsonArray) {


                        preference!!.setCredentials(it.get("payload").toString())
                        Utils.showLog(it.get("payload").toString())

                        // spinner_credentials.adapter = CredentialsAdapter(this, credentialsList)

                    } else {
                        Utils.showToast(this, getString(R.string.msg_common_error))


                    }


                } else {
                    if (it.has("message")) {
                        Utils.showToast(this, it.get("message").asString)


                    }
                }
            }


        })


    }


    private fun sendTokenToServer(token: String) {

        preference!!.setDeviceToken(token)
        dashBoardViewModel!!.updateDeviceToken(this, user!!._id, preference!!.getAuthToken(), token)
            .observe(this, Observer {


                if (it != null && it.has("status") && it.get("status").asString.equals("1")) {

                    if (it.has("user_info") && it.get("user_info") is JsonObject) {
                        var userInfo = it.get("user_info").asJsonObject


                        var user = AppPreference.getInstance(this).getUserData()

                        user!!.deviceToken = userInfo.get("deviceToken").asString

                        AppPreference.getInstance(this).setUserData(Gson().toJson(user).toString())


                    }


                } else {
                    if (it != null && it.has("message"))
                        Utils.showToast(this, it.get("message").asString)
                }

            })

    }

    var dashBoardViewModel: DashBoardViewModel? = null

    var preference: AppPreference? = null
    var user: UserModel? = null
    private fun initView() {


        dashBoardViewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)



        getCredentials()

        iv_msg.setOnClickListener(this)
        iv_profile_pic.setOnClickListener(this)




        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_contact_provider.visibility = View.VISIBLE

        }, 500)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_patient_referral.visibility = View.VISIBLE

        }, 600)
        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_education_videos.visibility = View.VISIBLE

        }, 700)
        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_satisfaction_survey.visibility = View.VISIBLE

        }, 800)





        cv_contact_provider.setOnClickListener(this)
        cv_patient_referral.setOnClickListener(this)
        cv_education_videos.setOnClickListener(this)
        cv_satisfaction_survey.setOnClickListener(this)
        iv_notification.setOnClickListener(this)
        cv_req_med_refill.setOnClickListener(this)
        cv_patient_consent.setOnClickListener(this)
        cv_review_provider_schedule.setOnClickListener(this)
        cv_psychometric.setOnClickListener(this)
        cv_meditation_survey.setOnClickListener(this)
        cv_patient_document.setOnClickListener(this)
        cv_contact_user.setOnClickListener(this)
        cv_employee_receipt.setOnClickListener(this)



        if (AppPreference.getInstance(this).getNotificationReadStatus() != null && AppPreference.getInstance(this
            ).getNotificationReadStatus()
        ) {
            tv_not_dot.visibility = View.VISIBLE
        } else {
            tv_not_dot.visibility = View.GONE
        }


    }


    override fun onBackPressed() {
        //  super.onBackPressed()

        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.msg_exit_from_app))
            .setPositiveButton(resources.getString(R.string.text_yes),
                DialogInterface.OnClickListener { dialog, which ->

                    finish()
                })
            .setNegativeButton(resources.getString(R.string.text_no),
                DialogInterface.OnClickListener { dialog, which ->

                    dialog.dismiss()
                })
            .show()

    }


    fun deleteOlderConversation() {


    }


    fun openPatientReferralDialog() {
        var dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_patient_referal_options)
        dialog!!.show()
        dialog!!.getWindow()!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.getWindow()!!.setGravity(Gravity.CENTER)


        var tvOptionNursing = dialog.findViewById<TextView>(R.id.tv_option_nursing)
        var tvOptionHospital = dialog.findViewById<TextView>(R.id.tv_option_hospital)


        tvOptionNursing.setOnClickListener {

            dialog.dismiss()
            startActivity(Intent(this, PatientReferralsActivity::class.java).putExtra("type", "1"))
        }

        tvOptionHospital.setOnClickListener {

            dialog.dismiss()
            startActivity(Intent(this, PatientReferralsActivity::class.java).putExtra("type", "2"))
        }


//        startActivity(Intent(this, PatientReferralsActivity::class.java))


    }


}
