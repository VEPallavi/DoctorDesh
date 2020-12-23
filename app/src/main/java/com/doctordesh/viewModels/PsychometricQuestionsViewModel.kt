package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.activity.QuestAns
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class PsychometricQuestionsViewModel : ViewModel() {
    private var psychometricQuestionsResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    private var psychometricResult: MutableLiveData<JsonObject>? = null


    fun getPsychometricQuestions(
        mContext: Context,
        categoryID: String
    ): MutableLiveData<JsonObject> {
        psychometricQuestionsResult = MutableLiveData()
        preference = AppPreference.getInstance(mContext)

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val token = preference!!.getAuthToken()
        var call = apiService.getPsychQuestions(token, categoryID)


        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()

                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {
                    psychometricQuestionsResult!!.setValue(response.body())

                    Utils.showLog("URL : " + response.raw().request().url())
                }
            }

        })


        return psychometricQuestionsResult!!

    }

    fun sendPsychometricResult(
        mContext: Context,
        categoryID: String,
        patientName: String,
        marks: String,
        dob: String,
        answers: ArrayList<JSONObject>
    ): MutableLiveData<JsonObject> {
        psychometricResult = MutableLiveData()
        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val token = preference!!.getAuthToken()
        var call = apiService.submitPsychAnsResult(
            token,
            userModel!!._id,
            categoryID,
            patientName,
            marks,
            dob,
             answers
        )

        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()

                if (response.code() == 401) {
                    Utils.showTokeExpireDialog(mContext)
                } else {

                    psychometricResult!!.value = response.body()

                    Utils.showLog("URL : " + response.raw().request().url())
                }
            }

        })


        return psychometricResult!!
    }





}