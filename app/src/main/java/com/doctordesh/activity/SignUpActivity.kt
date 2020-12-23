package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.adapters.CredentialsAdapter
import com.doctordesh.fragments.VerifyOTPDialogFragment
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.CredentialModel
import com.doctordesh.models.SignupRequestModel
import com.doctordesh.models.UserModel
import com.doctordesh.viewModels.SignupViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject

class SignUpActivity : AppCompatActivity(), View.OnClickListener, VerifyOTPDialogFragment.OnButtonClick {

    override fun onDismiss() {
        bottomSheetDialogFragment = null
    }

    override fun onResendClick() {
        bottomSheetDialogFragment!!.dismiss()
        signupUser()
    }

    override fun onSubmitClick() {

        Utils.hideSoftKeyboard(this)

        bottomSheetDialogFragment!!.dismiss()

        signupViewModel!!.verifyOTP(
            this, SignupRequestModel(
                et_first_name.text.toString(),
                et_last_name.text.toString(),
                et_email_id.text.toString(),
                selectedCredential!!.credential_type,
                et_password.text.toString(),
                otp
            )
        ).observe(this, Observer {
            Utils.showLog(it.toString()!!)

            if (it.has("status") && it.get("status").asString.equals("1")) {


                Utils.showToast(this, getString(R.string.msg_user_registered))

                Handler().postDelayed(Runnable {

                    startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    finish()

                    /*if (it.has("token")) {
                        AppPreference.getInstance(this).setAuthToken(it.get("token").toString())

                    }


                    if (it.has("userinfo") && it.get("userinfo") is JsonObject) {

                        AppPreference.getInstance(this).setUserData(it.get("userinfo").toString())


                        startActivity(Intent(this@SignUpActivity, DashboardActivity::class.java))
                        finish()


                    }*/


                }, 800)


            } else
                Utils.showToast(this, getString(R.string.msg_common_error))


        })


    }

    fun signupUser() {

        Utils.hideSoftKeyboard(this)

        if (checkValidations()) {

            signupViewModel!!.signupUser(
                this,
                SignupRequestModel(
                    et_first_name.text.toString(),
                    et_last_name.text.toString(),
                    et_email_id.text.toString(),
                    selectedCredential!!.credential_type,
                    et_password.text.toString(),
                    ""

                )
            ).observe(this, Observer {
                //    Utils.showLog(it.toString()!!)

                if (it.has("status") && it.get("status").asString.equals("1")) {
                    if (it.has("otp")) {

                        Utils.showToast(this, getString(R.string.msg_otp_sent))


                        otp = it.get("otp").asString
                            bottomSheetDialogFragment = VerifyOTPDialogFragment.newInstance(otp, this)
                            bottomSheetDialogFragment!!.show(supportFragmentManager, "VerifyOTPDialogFragment")



                    } else
                        Utils.showToast(this, getString(R.string.msg_common_error))
                }
                else {
                    if (it.has("message"))
                        Utils.showToast(this, it.get("message").asString)
                }


            })


        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.iv_password_toggle -> {
                if (showPassword) {
                    et_password.transformationMethod = PasswordTransformationMethod.getInstance()

                    iv_password_toggle.setImageResource(R.drawable.eye_hide)
                } else {

                    et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    iv_password_toggle.setImageResource(R.drawable.eye)
                }
                et_password.setSelection(et_password.text!!.length)
                showPassword = !showPassword
            }

            R.id.btn_save -> {
                signupUser()
            }
            R.id.tv_sign_in -> {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            }


        }
    }

    var otp: String = ""
    var showPassword: Boolean = false
    var signupViewModel: SignupViewModel? = null
    var bottomSheetDialogFragment: VerifyOTPDialogFragment? = null
    var credentialsList = ArrayList<CredentialModel>()
    var credentialsAdapter: CredentialsAdapter? = null
    var selectedCredential: CredentialModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initView()

    }

    private fun initView() {
        toolbar.title = ""
        // toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // toolbar.setNavigationOnClickListener { finish() }
        iv_password_toggle.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        tv_sign_in.setOnClickListener(this)
        credentialsList.add(CredentialModel("00", "Select your credential"))
        selectedCredential = credentialsList.get(0)
        credentialsAdapter = CredentialsAdapter(this, credentialsList)
        spinner_credentials.adapter = credentialsAdapter

        /*  spinner_credentials.setOnItemClickListener(
              object : AdapterView.OnItemClickListener {
                  override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                      selectedCredential = credentialsList.get(position)

                  }
              }
          )*/


        spinner_credentials.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedCredential = credentialsList.get(position)
            }
        }

        signupViewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)



        signupViewModel!!.getCredentials(this).observe(this, Observer {

            if (it != null) {
                if (it.has("status") && it.get("status").asString.equals("1")) {

                    if (it.has("payload") && it.get("payload") is JsonArray) {


                        val type = object : TypeToken<List<CredentialModel>>() {}.type

                        var tempCredentialsList =
                            Gson().fromJson<List<CredentialModel>>(it.get("payload").toString(), type)

                        if (tempCredentialsList != null && tempCredentialsList.size > 0) {
                            credentialsList.addAll(tempCredentialsList)
                            credentialsAdapter!!.notifyDataSetChanged()
                        }

                        // spinner_credentials.adapter = CredentialsAdapter(this, credentialsList)

                    } else {
                        Utils.showToast(this, getString(R.string.msg_common_error))

                        Handler().postDelayed(Runnable {
                            finish()

                        }, 900)

                    }


                } else {
                    if (it.has("message")) {
                        Utils.showToast(this, it.get("message").asString)

                        Handler().postDelayed(Runnable {
                            finish()

                        }, 900)

                    }
                }
            }


        })


    }


    private fun checkValidations(): Boolean {
        if (!Utils.isInternetAvailable(this)) {
            Utils.showToast(this, resources.getString(R.string.msg_no_internet))
            return false
        } else if (et_first_name.text!!.length < 2) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_name))
            return false
        } else if (et_last_name.text!!.length < 2) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_name))
            return false
        } else if (!Utils.isEmailValid(et_email_id.text.toString())) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_email))
            return false
        } else if (selectedCredential!!._id.equals("00")) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_credential))
            return false
        } else if (et_password.text!!.length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pass))
            return false
        } else if (et_password.text!!.length < 6) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_pass))
            return false

        }

        return true
    }


}
