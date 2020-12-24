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

class ContactUserViewModel : ViewModel(){

    var contactUserData: MutableLiveData<JsonObject>? = null


    fun getData(mContext: Context): MutableLiveData<JsonObject> {
        contactUserData = MutableLiveData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)
        var preference = AppPreference.getInstance(mContext)
        val token = preference.getAuthToken()


        return contactUserData!!
    }


}