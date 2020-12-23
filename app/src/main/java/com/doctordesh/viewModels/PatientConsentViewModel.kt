package com.doctordesh.viewModels

import android.content.Context
import android.graphics.Bitmap
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


class PatientConsentViewModel : ViewModel() {
    private var consentResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    fun sendPatientConsent(
        ccm: String,
        bhi: String,
        patientName: String,
        facilityName: String,
        room: String,
        verbalConsent: String,
        careNavigator: String,
        date: String,
        signature: MultipartBody.Part,
        mContext: Context
    ): MutableLiveData<JsonObject> {


        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()

        consentResult = MutableLiveData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val userID = RequestBody.create(MediaType.parse("multipart/form-data"), userModel!!._id)
        val patientname = RequestBody.create(MediaType.parse("multipart/form-data"), patientName)
        val ccmconsent = RequestBody.create(MediaType.parse("multipart/form-data"), ccm)
        val bhiconsent = RequestBody.create(MediaType.parse("multipart/form-data"), bhi)
        val facilityname = RequestBody.create(MediaType.parse("multipart/form-data"), facilityName)
        val roomNumber = RequestBody.create(MediaType.parse("multipart/form-data"), room)
        val verbalconsent = RequestBody.create(MediaType.parse("multipart/form-data"), verbalConsent)
        val carenavigator = RequestBody.create(MediaType.parse("multipart/form-data"), careNavigator)


        var call = apiService.sentPatientConsent(token, userID, patientname, ccmconsent, bhiconsent,facilityname,roomNumber,verbalconsent,carenavigator,signature)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                consentResult!!.setValue(response.body())
            }

        })


        return consentResult!!
    }
}