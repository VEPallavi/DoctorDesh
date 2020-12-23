package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class ProviderScheduleViewModel : ViewModel() {
    private var providerScheduleResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null


    fun getProviderSchedule(mContext: Context, providerId: String): MutableLiveData<JsonObject> {

        preference = AppPreference.getInstance(mContext)

        providerScheduleResult = MutableLiveData()


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val token = preference!!.getAuthToken()
        var call = apiService.providerSchedule(token, providerId)






        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()

                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {

                    providerScheduleResult!!.setValue(response.body())

                    Utils.showLog("UTL : " + response.raw().request().url())
                }
            }

        })



        return providerScheduleResult!!

    }


}