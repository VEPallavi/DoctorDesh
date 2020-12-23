package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.SavePasswordViewModel
import kotlinx.android.synthetic.main.activity_save_password.*

class SavePasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.btn_save -> {
                if (checkValidation()) {

                    savePasswordViewModel!!.savePassword(email,et_pass.text.toString(),this).observe(this, Observer {


                        Utils.showLog(it.toString())
                        if (it.has("status") && it.get("status").asString.equals("1")) {

                            if (it.has("message"))
                                Utils.showToast(this, it.get("message").asString)


                                Handler().postDelayed(Runnable {

                                    val intent = Intent(this@SavePasswordActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)

                                }, 600)




                        } else {
                            if (it.has("message"))
                                Utils.showToast(this, it.get("message").asString)
                        }
                    })






                }

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


    private fun checkValidation(): Boolean {
        if (et_pass.text!!.toString().length == 0) {
            Utils.showToast(this, resources.getString(R.string.msg_empty_pass))
            return false
        } else if (et_pass.text!!.toString().length < 6) {
            Utils.showToast(this, resources.getString(R.string.msg_invalid_pass))
            return false
        }

        return true
    }


    var showPassword: Boolean = false
    var email = ""
    var savePasswordViewModel: SavePasswordViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_password)
        initView()
    }

    fun initView() {

        savePasswordViewModel = ViewModelProviders.of(this).get(SavePasswordViewModel::class.java)
        if (intent.hasExtra("email"))
            email = intent.getStringExtra("email")


        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        btn_save.setOnClickListener(this)
        iv_password_toggle.setOnClickListener(this)
    }

}
