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

class EducationalVideoViewModel : ViewModel() {

    var videoListResponse: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null
    fun getVideoList(mContext: Context, url: String): MutableLiveData<JsonObject> {

        if (videoListResponse == null) {
            videoListResponse = MutableLiveData()
        }


        preference = AppPreference.getInstance(mContext)
        val token = preference!!.getAuthToken()


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.getVideosList(url, token)
        Utils.showProgressDialog(mContext)


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()


                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else
                    videoListResponse!!.setValue(response.body())
            }

        })



        return videoListResponse!!
    }


}