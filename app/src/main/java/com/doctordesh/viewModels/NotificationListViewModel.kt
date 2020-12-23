package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.UserModel
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class NotificationListViewModel : ViewModel() {

    var notificationsListResponse: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null
    var user: UserModel? = null

    fun getNotificationsList(mContext: Context): MutableLiveData<JsonObject> {
        notificationsListResponse = MutableLiveData<JsonObject>()


        preference = AppPreference.getInstance(mContext)
        user = preference!!.getUserData()
        val token = preference!!.getAuthToken()


        var apiService = ApiClient.getClient().create(ApiService::class.java)
        var call = apiService.getNotifications(token,user!!._id)
        Utils.showProgressDialog(mContext)


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                notificationsListResponse!!.setValue(response.body())
            }

        })






        return notificationsListResponse!!
    }
}