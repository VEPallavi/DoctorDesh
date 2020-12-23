package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardViewModel : ViewModel() {
    private var deviceTokenResult: MutableLiveData<JsonObject>? = null

    private var credentialsResult: MutableLiveData<JsonObject>? = null

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
        params.put("device_type","android")

        var call = apiService.updateProfile(token, params)

        call.enqueue(object : retrofit2.Callback<JsonObject>    {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()


                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {

                deviceTokenResult!!.setValue(response.body())
            }}

        })

        return deviceTokenResult!!

    }


    fun getCredentials( mContext: Context) : MutableLiveData<JsonObject>
    {

        if (credentialsResult == null)
            credentialsResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.getCredentials()
      //  Utils.showProgressDialog(mContext)


        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
               // Utils.hideProgressDialog()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
             //   Utils.hideProgressDialog()
                if (response != null && response.body() != null)
                    credentialsResult!!.value = response.body()
                else
                    Utils.showToast(mContext, mContext.resources.getString(R.string.msg_common_error))
            }
        })

        return credentialsResult!!
    }











}