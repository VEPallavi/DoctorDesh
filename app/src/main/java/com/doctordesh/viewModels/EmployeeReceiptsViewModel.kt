package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response


class EmployeeReceiptsViewModel : ViewModel(){
    private var sendEmployeeReceiptsResponse: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null



    fun sendEmployeeReceipts(mContext: Context, employeeName: String, type: String, amount: String, receiptList: ArrayList<MultipartBody.Part>): MutableLiveData<JsonObject> {
        sendEmployeeReceiptsResponse= MutableLiveData()
        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)
        val token = preference!!.getAuthToken()

        val userID = RequestBody.create(MediaType.parse("multipart/form-data"), userModel!!._id)
        val employeename = RequestBody.create(MediaType.parse("multipart/form-data"), employeeName)
        val type = RequestBody.create(MediaType.parse("multipart/form-data"), type)
        val amount = RequestBody.create(MediaType.parse("multipart/form-data"), amount)

        var call = apiService.sendEmployeeReceipts(token, userID, employeename, type, amount, receiptList)

        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else{

                    Utils.hideProgressDialog()
                    sendEmployeeReceiptsResponse!!.setValue(response.body())
                }}
        })

        return sendEmployeeReceiptsResponse!!
    }


}