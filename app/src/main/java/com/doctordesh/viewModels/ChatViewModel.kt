package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.AppPreference
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private var sendNotificationResult: MutableLiveData<JsonObject>? = null


    fun sendNotification(mContext: Context, userId : String, userName: String, userMessage: String, deviceToken: String): MutableLiveData<JsonObject> {
        sendNotificationResult = MutableLiveData()


        var apiService = ApiClient.getClient().create(ApiService::class.java)
        var preference = AppPreference.getInstance(mContext)
        val token = preference.getAuthToken()

        var call = apiService.sendNotification(token,deviceToken, userName, userMessage,userId)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                sendNotificationResult!!.value=response.body()

            }

        })

        return sendNotificationResult!!
    }


}