package com.doctordesh.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_verify_otp.*

class VerifyOTPDialogFragment() : BottomSheetDialogFragment() {

    var onButtonClick: OnButtonClick? = null

    @SuppressLint("ValidFragment")
    constructor(onButtonClick: OnButtonClick) : this() {
        this.onButtonClick = onButtonClick

    }


    companion object {

        fun newInstance(OTP: String, onButtonClick: OnButtonClick): VerifyOTPDialogFragment {


            var dialogFragment = VerifyOTPDialogFragment(onButtonClick)
            var args = Bundle()
            args.putString("otp", OTP)
            dialogFragment.arguments = args

            return dialogFragment

        }

    }

    var OTP = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_verify_otp, container, false);
        OTP = arguments!!.get("otp") as String

        var btnVerify = view.findViewById<Button>(R.id.btn_verify)
        btnVerify.setOnClickListener(View.OnClickListener {
            if (checkValidations()) {
                onButtonClick!!.onSubmitClick()
            }

        })

        var tvResend = view.findViewById<TextView>(R.id.tv_resend)
        tvResend.setOnClickListener(View.OnClickListener {

            onButtonClick!!.onResendClick()
        })

        var et_code_1 = view.findViewById<EditText>(R.id.et_code_1)
        et_code_1.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length==1)
                {
                    et_code_1.clearFocus()
                    et_code_2.requestFocus()
                    et_code_2.isCursorVisible=true
                }
            }

        })
        var et_code_2 = view.findViewById<EditText>(R.id.et_code_2)
        et_code_2.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length==1)
                {
                    et_code_2.clearFocus()
                    et_code_3.requestFocus()
                    et_code_3.isCursorVisible=true
                }
            }

        })
        var et_code_3 = view.findViewById<EditText>(R.id.et_code_3)
        et_code_3.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length==1)
                {
                    et_code_3.clearFocus()
                    et_code_4.requestFocus()
                    et_code_4.isCursorVisible=true
                }
            }

        })


        return view
    }


    fun checkValidations(): Boolean {
        if (!Utils.isInternetAvailable(context)) {
            Utils.showToast(context!!, resources.getString(R.string.msg_no_internet))
            return false
        } else if (et_code_1.text!!.length == 0 || et_code_2.text!!.length == 0 || et_code_3.text!!.length == 0 || et_code_4.text!!.length == 0) {
            Utils.showToast(context!!, resources.getString(R.string.msg_invalid_otp))
            return false
        } else if (!OTP.equals(et_code_1.text!!.toString() + et_code_2.text!!.toString() + et_code_3.text!!.toString() + et_code_4.text!!.toString())) {
            Utils.showToast(context!!, resources.getString(R.string.msg_invalid_otp))
            return false
        }

        return true

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onButtonClick!!.onDismiss()

    }


    interface OnButtonClick {
        fun onDismiss()
        fun onResendClick()
        fun onSubmitClick()

    }


}