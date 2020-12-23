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

class MeditationWellnessViewModel : ViewModel() {

    var meditationData: MutableLiveData<JsonObject>? = null


    fun getData(mContext: Context): MutableLiveData<JsonObject> {
        meditationData = MutableLiveData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)
        var preference = AppPreference.getInstance(mContext)
        val token = preference.getAuthToken()

        var call = apiService.getMeditationData(token)


        Utils.showProgressDialog(mContext)


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.hideProgressDialog()

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                Utils.hideProgressDialog()
                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {

                    meditationData!!.value = response.body()
                }

            }

        })

        return meditationData!!
    }


}