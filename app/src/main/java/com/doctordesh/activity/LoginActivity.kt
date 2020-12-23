package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.UserModel
import com.doctordesh.viewModels.LoginViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {


    override fun onClick(view: View?) {


        when (view?.id) {
            R.id.tv_forgot_pass -> {
                var intent = Intent(this@LoginActivity, EmailVerificationActivity::class.java)
                intent.putExtra("from", 2);
                startActivity(intent)
            }
            R.id.btn_login -> {

                if (checkValidation()) {


                    loginViewModel!!.loginUser(et_email_id.text.toString(), et_pass.text.toString(), this)
                        .observe(this, Observer {

                            Utils.showLog(it.toString())


                            if (it.has("status") && it.get("status").asString.equals("1")) {

                                if (it.has("token")) {
                                    AppPreference.getInstance(this).setAuthToken(it.get("token").asString)

                                }


                                if (it.has("data") && it.get("data") is JsonObject) {
                                    val type = object : TypeToken<UserModel>() {}.type
                                    //  var user = Gson().fromJson<UserModel>(it.get("data").asString, type)

                                    AppPreference.getInstance(this).setUserData(it.get("data").toString())


                                    var user = AppPreference.getInstance(this).getUserData() as UserModel

                                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                                    finish()

                                }


                            } else {
                                if (it.has("message"))
                                    Utils.showToast(this, it.get("message").asString)
                            }


                        })


                    /*  val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                      startActivity(intent)
                      finish()*/
                }
            }
            R.id.tv_sign_up -> {

                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                finish()
            }
            R.id.iv_password_toggle -> {
                if (showPassword) {
                    et_pass.transformationMethod = PasswordTransformationMethod.getInstance()

                    iv_password_toggle.setImageResource(R.drawable.eye_hide)
                } else {

                    et_pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    iv_password_toggle.setImageResource(R.drawable.eye)
                }
                et_pass.setSelection(et_pass.text!!.length)
                showPassword = !showPassword
            }

        }
    }


    var showPassword: Boolean = false
    var loginViewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }

    private fun initView() {

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)


       /* et_email_id.setText("samsrules@gmail.com")
        et_pass.setText("123456")*/

        tv_forgot_pass.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_sign_up.setOnClickListener(this)
        iv_password_toggle.setOnClickListener(this)
    }


    private fun checkValidation(): Boolean {
        if (!Utils.isEmailValid(et_email_id.text.toString())) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_email))
            return false
        } else if (et_pass.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pass))
            return false
        } else if (et_pass.text!!.toString().length < 6) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_pass))
            return false
        }

        return true
    }


}
