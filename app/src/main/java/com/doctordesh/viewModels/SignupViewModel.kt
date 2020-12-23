package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.models.SignupRequestModel
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class SignupViewModel : ViewModel() {

    private var signupResult: MutableLiveData<JsonObject>? = null
    private var verifyOTPResult: MutableLiveData<JsonObject>? = null
    private var credentialsResult: MutableLiveData<JsonObject>? = null

    public fun signupUser(mContext: Context, signupRequestModel: SignupRequestModel): MutableLiveData<JsonObject> {
        if (signupResult == null) {
            signupResult = MutableLiveData()
        }


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.signupUser(
            signupRequestModel.firstName,
            signupRequestModel.lastName,
            signupRequestModel.credentials,
            signupRequestModel.email,
            signupRequestModel.password
        )
        Utils.showProgressDialog(mContext)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                if (response != null && response.body() != null)
                    signupResult!!.setValue(response.body())
                else
                    Utils.showToast(mContext, mContext.resources.getString(R.string.msg_common_error))
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }
        })



        return signupResult!!

    }

    fun verifyOTP(mContext: Context, signupRequestModel: SignupRequestModel): MutableLiveData<JsonObject> {

        if (verifyOTPResult == null)
            verifyOTPResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.verifySignupUser(
            signupRequestModel.firstName,
            signupRequestModel.lastName,
            signupRequestModel.credentials,
            signupRequestModel.email,
            signupRequestModel.password,
            signupRequestModel.otp
        )
        Utils.showProgressDialog(mContext)


        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                if (response != null && response.body() != null)
                    verifyOTPResult!!.value = response.body()
                else
                    Utils.showToast(mContext, mContext.resources.getString(R.string.msg_common_error))
            }

        })


        return verifyOTPResult!!
    }

    fun getCredentials(mContext: Context): MutableLiveData<JsonObject> {

        if (credentialsResult == null)
            credentialsResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.getCredentials()
        Utils.showProgressDialog(mContext)


        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                if (response != null && response.body() != null)
                    credentialsResult!!.value = response.body()
                else
                    Utils.showToast(mContext, mContext.resources.getString(R.string.msg_common_error))
            }
        })

        return credentialsResult!!
    }
}