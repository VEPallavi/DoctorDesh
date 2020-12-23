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

class PsychometricScaleViewModel:ViewModel()
{
    private var psychometricScaleResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    fun getPsychometricScales(mContext:Context) : MutableLiveData<JsonObject>
    {




        preference = AppPreference.getInstance(mContext)

        psychometricScaleResult=MutableLiveData()


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val token = preference!!.getAuthToken()
        var call = apiService.getPsychCategories(token);

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()

                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {
                    psychometricScaleResult!!.setValue(response.body())

                    Utils.showLog("URL : " + response.raw().request().url())
                }
            }

        })



        return psychometricScaleResult!!
    }


}