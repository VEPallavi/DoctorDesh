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

class MedRefillViewModel : ViewModel()
{
    private var patientReferralResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null



    fun sentMedicalRefillRequest(
        mContext: Context,
        patientName: String,
        patientDOB: String,
        patientLocation: String,
        medicineName: String,
        doseMedicine: String,
        frequencyDose: String,
        pharmacyName: String,
        pharmacyFaxNumber: String,
        pharmacyNumber: String,
        faxNo: String,
        prnText: String
    ): MutableLiveData<JsonObject> {

        if (patientReferralResult == null)
            patientReferralResult = MutableLiveData()


        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()


        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.submitMedRquestRefill(token, patientName, patientDOB, patientLocation, medicineName,doseMedicine,frequencyDose,pharmacyName,pharmacyFaxNumber,pharmacyNumber,faxNo,prnText, userModel!!._id)
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