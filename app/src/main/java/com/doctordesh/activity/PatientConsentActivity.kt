package com.doctordesh.activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.PatientConsentViewModel
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.activity_patient_consent.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class PatientConsentActivity : AppCompatActivity(), View.OnClickListener {

    var file: File? = null
    var patientConsentViewModel: PatientConsentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_consent)
        initView()
    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
                return true
            } else {
                return false
            }

        } else {
            return true
        }


    }

    fun requestPermission() {

        var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        ActivityCompat.requestPermissions(this, permissions, 123);


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (grantResults.size > 0) {

            var CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            var readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            var writeExternalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (CameraPermission && readExternalStorage && writeExternalStorage) {

            } else {
                requestPermission()
                Utils.showToast(this, getString(R.string.msg_incomplete_permissions))
            }

        }


    }

    fun initView() {

        patientConsentViewModel =
            ViewModelProviders.of(this).get(PatientConsentViewModel::class.java)
        if (!checkPermission()) {
            requestPermission()
        }

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener { finish() }
        tv_bhi.setOnClickListener(this)
        tv_ccm.setOnClickListener(this)
        tv_clear.setOnClickListener(this)
        tv_save.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

        signature_pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {
                //Utils.showToast(this@PatientConsentActivity,"Signed saved successfully")
            }

            override fun onClear() {

            }
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_save -> {
                if (signature_pad.isEmpty) {
                    Utils.showToast(this, "Please add signature first.")
                } else {
                    if (saveSignatureInDevice(signature_pad.signatureBitmap)) {
                        Utils.showToast(this, "Image saved successfully")
                    } else {
                        Utils.showToast(this, "Image not saved")
                    }

                }
            }
            R.id.tv_clear -> {
                signature_pad.clearView()
                file = null
            }
            R.id.tv_ccm -> openDetailDialog(
                getString(R.string.txt_chronic_care_management),
                getString(R.string.txt_ccm_detail)
            )
            R.id.tv_bhi -> openDetailDialog(
                getString(R.string.txt_behavioral_health_integration),
                getString(R.string.txt_bhi_detail)
            )
            R.id.btn_submit -> {
                if (checkValidation()) {


                    var requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                    var img = MultipartBody.Part.createFormData(
                        "patientSignature",
                        file!!.name,
                        requestFile
                    )


                    var ccmValue = ""
                    if (rg_ccm.checkedRadioButtonId == R.id.rb_ccm_option_1)
                        ccmValue = "1"
                    else
                        ccmValue = "2"

                    var bhiValue = ""
                    if (rg_bhi.checkedRadioButtonId == R.id.rb_bhi_option_1)
                        bhiValue = "1"
                    else
                        bhiValue = "2"


                    patientConsentViewModel!!.sendPatientConsent(
                        ccmValue,
                        bhiValue,
                        et_patient_name.text.toString(),
                        et_facility_name.text.toString(),
                        et_room_number.text.toString(),
                        et_verbal_consent.text.toString(),
                        et_care_navigator.text.toString(),
                        "",
                        img,
                        this
                    ).observe(this, Observer {

                        if (it != null && it.has("status") && it.get("status").asString.equals("1")) {
                            Utils.showLog(it.toString())
                            Utils.showToast(this, "Patient consent request added successfully.")
                            rg_bhi.clearCheck()
                            rg_ccm.clearCheck()
                            et_patient_name.setText("")
                            et_facility_name.setText("")
                            et_room_number.setText("")
                            et_verbal_consent.setText("")
                            et_care_navigator.setText("")
                            signature_pad.clearView()
                            Handler().postDelayed(Runnable { finish() }, 3000)


                        }


                    })


                }


            }
        }
    }

    fun saveSignatureInDevice(signature: Bitmap?): Boolean {
        var result = false
        try {

            /*    val mediaStorageDir = File(
                    Environment.getExternalStorageDirectory().toString() + "/Android/data/" + applicationContext.packageName + "/Files/PatientDocument")
    */


            val mediaStorageDir = File(filesDir, "doctordesh" + File.separator + "images")


            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {

                }
            }


            val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
            val fileName = "signature_$timeStamp.jpg"
            val pictureFile = File(mediaStorageDir, fileName)

            file = pictureFile

            if (file!!.exists())
                file!!.delete()


            try {
                var out = FileOutputStream(file);
                signature!!.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.flush();
                out.close();
            } catch (e: Exception) {
                e.printStackTrace();
            }

            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }




        return result
    }


    private fun checkValidation(): Boolean {
        if (!Utils.isInternetAvailable(this)) {

            Utils.showToast(this, getString(R.string.msg_no_internet))
            return false
        } else if (rg_ccm.checkedRadioButtonId == -1) {

            Utils.showToast(this, getString(R.string.msg_select_ccm))
            return false
        } else if (rg_bhi.checkedRadioButtonId == -1) {

            Utils.showToast(this, getString(R.string.msg_select_bhi))
            return false
        } else if (et_patient_name.text.toString() == "") {
            Utils.showToast(this, getString(R.string.msg_empty_patient_name))
            return false
        } else if (et_facility_name.text.toString() == "") {
            Utils.showToast(this, getString(R.string.msg_empty_facility_name))
            return false
        } else if (et_room_number.text.toString() == "") {
            Utils.showToast(this, getString(R.string.msg_empty_room))
            return false
        } else if (signature_pad.isEmpty) {
            Utils.showToast(this, getString(R.string.msg_add_signature))
            return false
        } else if (file == null) {
            Utils.showToast(this, getString(R.string.msg_save_signature))
            return false
        } else if (et_verbal_consent.text.toString() == "") {
            Utils.showToast(this, getString(R.string.msg_enter_verbal_consent_name))
            return false
        } else if (et_care_navigator.text.toString() == "") {
            Utils.showToast(this, getString(R.string.msg_enter_care_navigator))
            return false
        }

        return true
    }

    private fun openDetailDialog(title: String, message: String) {
        var dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_patient_consent)
        dialog!!.show()
        dialog!!.getWindow()!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.getWindow()!!.setGravity(Gravity.CENTER)


        var ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { dialog.dismiss() }
        var tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = title

        var tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = message


    }


}