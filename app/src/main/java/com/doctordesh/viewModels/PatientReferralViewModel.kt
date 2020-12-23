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

class PatientReferralViewModel : ViewModel() {

    private var patientReferralResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    fun sentPatientReferalRequest(
        mContext: Context,
        patientName: String,
        facilityName: String,
        roomNumber: String,
        reason: String,
        refferalType: String
    ): MutableLiveData<JsonObject> {


            patientReferralResult = MutableLiveData()


        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.submitPatientReferal(token, patientName, facilityName, roomNumber, reason, userModel!!._id,refferalType)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                patientReferralResult!!.setValue(response.body())
            }

        })







        return patientReferralResult!!
    }


}