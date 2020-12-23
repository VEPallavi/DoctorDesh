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

class ProvidersDashboardViewModel: ViewModel()
{


    private var deviceTokenResult: MutableLiveData<JsonObject>? = null

    fun updateDeviceToken(
        mContext: Context,
        userId: String,
        token: String,
        devicetoken: String
    ): MutableLiveData<JsonObject> {

        if (deviceTokenResult == null)
            deviceTokenResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)


        var params = HashMap<String, String>()
        params.put("user_id", userId)
        params.put("device_token", devicetoken)

        var call = apiService.updateProfile(token, params)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                deviceTokenResult!!.setValue(response.body())
            }

        })








        return deviceTokenResult!!


    }




}