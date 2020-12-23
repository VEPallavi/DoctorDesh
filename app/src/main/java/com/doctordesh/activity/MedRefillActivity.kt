package com.doctordesh.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.MedRefillViewModel

import kotlinx.android.synthetic.main.activity_med_refill.*
import androidx.lifecycle.Observer
import java.util.Calendar





class MedRefillActivity : AppCompatActivity() {


    private var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    var selectedDate=""
    var medRefillViewModel: MedRefillViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_med_refill)
        initViews()
    }


    fun initViews()
    {

        medRefillViewModel = ViewModelProviders.of(this).get(MedRefillViewModel::class.java)

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener { finish() }


        tv_patient_dob.setOnClickListener(View.OnClickListener {

            openDatePicker()



        })


        sw_prn.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> run{

            if(isChecked)
                et_prn_reason.visibility=View.VISIBLE
            else
                et_prn_reason.visibility=View.GONE

        } })

btn_submit.setOnClickListener(View.OnClickListener {


    if(checkValidations())
    {
        medRefillViewModel!!.sentMedicalRefillRequest(this,et_patient_name.text.toString(),selectedDate,et_patient_location.text.toString(),et_medicine_name.text.toString(),
            et_dose_of_medicine.text.toString(),et_dose_frequency.text.toString(),et_pharmacy_name.text.toString(),et_pharmacy_fax_number.text.toString(),et_pharmacy_number.text.toString(),et_your_fax_number.text.toString()
        ,et_prn_reason.text.toString()).observe(this,
            Observer {

                if (it != null) {

                    Log.e("DoctorDesh","Med Refilll request result : "+it.toString())

                    if (it.has("status") && it.get("status").asString.equals("1")) {


Utils.showToast(this,"Your request submitted successfully")

                        Handler().postDelayed(Runnable { finish() },900)


                        //startActivity(Intent(this, PatientReferralsThanksActivity::class.java))
                    }
                }


            })
    }

})


    }


    fun openDatePicker()
    {

        val mCalendar=Calendar.getInstance()
        mMonth=mCalendar.get(Calendar.MONTH)
        mDay=mCalendar.get(Calendar.DAY_OF_MONTH)
        mYear=mCalendar.get(Calendar.YEAR)



        var datePickerDialog = DatePickerDialog(this,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth -> run{


                selectedDate=""+(monthOfYear+1)+"/"+dayOfMonth+"/"+mYear
                tv_patient_dob.text=selectedDate

            } },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())

        datePickerDialog.show()





    }


    fun checkValidations(): Boolean {

        if (!Utils.isInternetAvailable(this)) {
            Utils.showToast(this, resources.getString(R.string.msg_no_internet))
            return false
        } else if (et_patient_name.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_patient_name))
            return false
        } else if (selectedDate!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_patient_dob))
            return false
        } else if (et_patient_location.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_patient_location))
            return false
        } else if (et_medicine_name.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_medicine_name))
            return false
        }
        else if (et_dose_of_medicine.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_medicine_dose))
            return false
        }
        else if (et_dose_frequency.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_medicine_frequency))
            return false
        }
        else if (sw_prn.isChecked && et_prn_reason.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_prn))
            return false
        }
        else if (et_pharmacy_name.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pharmacy_name))
            return false
        }
        else if (et_pharmacy_number.text!!.toString().length <= 9) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pharmacy_number))
            return false
        }
        else if (et_pharmacy_fax_number.text!!.toString().length <= 9) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pharmacy_fax_number))
            return false
        }
        else if (et_your_fax_number.text!!.toString().length <= 9) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_your_fax_number))
            return false
        }

        return true
    }

}
