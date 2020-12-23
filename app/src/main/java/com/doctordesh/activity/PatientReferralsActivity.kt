package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.PatientReferralViewModel
import kotlinx.android.synthetic.main.activity_patient_referrals.*

class PatientReferralsActivity : AppCompatActivity() {


    var patientReferralViewModel: PatientReferralViewModel? = null
    var refferalType = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_referrals)
        initView()
    }

    private fun initView() {

        patientReferralViewModel =
            ViewModelProviders.of(this).get(PatientReferralViewModel::class.java)

        refferalType = intent.getStringExtra("type")
        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener { finish() }

        btn_submit.setOnClickListener(View.OnClickListener {
            if (checkValidations()) {
                patientReferralViewModel!!.sentPatientReferalRequest(
                    this,
                    et_patient_name.text.toString(),
                    et_facility_name.text.toString(),
                    et_room_number.text.toString(),
                    et_referral_reason.text.toString(),
                    refferalType
                ).observe(this,
                    Observer {

                        if (it != null) {
                            if (it.has("status") && it.get("status").asString.equals("1")) {


                                if (it.has("payload")) {

                                    var patientDetail = it.getAsJsonObject("payload")

                                    startActivity(
                                        Intent(
                                            this,
                                            PatientReferralsThanksActivity::class.java
                                        ).putExtra("patientID", patientDetail.get("_id").asString)
                                    )
                                } else {
                                    Utils.showToast(this, getString(R.string.msg_common_error))
                                }

                            }
                        }


                    })
            }


        })

        if(refferalType == "2")
        {
            et_facility_name.hint="Patient phone number"
            et_room_number.hint="Your name, practice name, and phone number"


        }


    }


    fun checkValidations(): Boolean {

        if (!Utils.isInternetAvailable(this)) {
            Utils.showToast(this, resources.getString(R.string.msg_no_internet))
            return false
        } else if (et_patient_name.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_patient_name))
            return false
        } else if (et_facility_name.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_facility_name))
            return false
        } else if (et_room_number.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_room))
            return false
        } else if (et_referral_reason.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_referral_reason))
            return false
        }

        return true
    }


}
