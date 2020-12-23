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

class StaffMemberListViewModel:ViewModel()
{
    private var staffMemberListResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null



    fun getStaffMemberList(mContext: Context, staffType: String): MutableLiveData<JsonObject>
    {
        staffMemberListResult= MutableLiveData()
        preference = AppPreference.getInstance(mContext)

        var apiService = ApiClient.getClient().create(ApiService::class.java)


        var call = apiService.getStaffMembers(preference!!.getAuthToken(),staffType)


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                staffMemberListResult!!.setValue(response.body())

                Utils.showLog("UTL : " + response.raw().request().url())

            }

        })





        return staffMemberListResult!!

    }






}