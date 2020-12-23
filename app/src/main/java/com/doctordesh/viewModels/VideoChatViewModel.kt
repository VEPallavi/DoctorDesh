package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ProviderModel
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class VideoChatViewModel : ViewModel() {
    private var inviteUserResult: MutableLiveData<JsonObject>? = null
    private var rejectVideoCallResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    fun inviteUser(mContext: Context, providerModel: ProviderModel): MutableLiveData<JsonObject> {

        preference = AppPreference.getInstance(mContext)

        inviteUserResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)
        val userModel = preference!!.getUserData()
        var call = apiService.inviteUser(
            preference!!.getAuthToken(),
            providerModel._id,
            "Call invite",
            "Hippa Calling Invitations."
        )


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                inviteUserResult!!.setValue(response.body())
            }

        })

        return inviteUserResult!!
    }


    fun rejectVideoCall(mContext: Context, userId: String): MutableLiveData<JsonObject> {

        preference = AppPreference.getInstance(mContext)

        rejectVideoCallResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)
        var call = apiService.rejectVideoCall(
            preference!!.getAuthToken(),
            userId,
            "Call invite",
            "Hippa Calling Invitations."
        )


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                rejectVideoCallResult!!.setValue(response.body())
            }

        })

        return rejectVideoCallResult!!
    }


}