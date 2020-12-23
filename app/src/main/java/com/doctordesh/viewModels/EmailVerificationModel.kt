package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class EmailVerificationModel : ViewModel() {
    private var emailVerificationResult: MutableLiveData<JsonObject>? = null

    fun verifyEmail(email: String, mContext: Context): MutableLiveData<JsonObject> {
      /*  if (emailVerificationResult == null) {
            emailVerificationResult = MutableLiveData()
        }*/
        emailVerificationResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.forgotPassword(email)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                emailVerificationResult!!.setValue(response.body())
            }

        })










        return emailVerificationResult!!
    }


}