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

class PatientReferralsThanksViewModel : ViewModel()
{

    var preference:AppPreference?=null

    private var picResult: MutableLiveData<JsonObject>? = null


    fun updatePatientPic(mContext: Context, patientID: String,pic: MultipartBody.Part): MutableLiveData<JsonObject> {
        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()

        if (picResult == null)
            picResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val id = RequestBody.create(MediaType.parse("multipart/form-data"), userModel!!._id)
        val patientID= RequestBody.create(MediaType.parse("multipart/form-data"),patientID)
        val filename=    RequestBody.create(MediaType.parse("multipart/form-data"), patientID.toString()+"image")
        var call = apiService.updatePatientPic(token, filename,patientID, pic)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                picResult!!.setValue(response.body())
            }

        })



        return picResult!!
    }


}