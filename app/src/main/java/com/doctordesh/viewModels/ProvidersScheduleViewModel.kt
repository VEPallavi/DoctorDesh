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

class ProvidersScheduleViewModel : ViewModel() {

    private var providersListResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null


    fun getProvidersList(mContext: Context): MutableLiveData<JsonObject> {
        providersListResult = MutableLiveData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)


        var call = apiService.getAllProviders()





        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                providersListResult!!.setValue(response.body())

                Utils.showLog("UTL : " + response.raw().request().url())

            }

        })



        return providersListResult!!
    }


}