package com.doctordesh.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private var loginResult: MutableLiveData<JsonObject>? = null


    fun loginUser(email: String, password: String, mContext: Context): MutableLiveData<JsonObject> {
       /* if (loginResult == null) {
            loginResult = MutableLiveData()
        }*/

        loginResult = MutableLiveData()
        var apiService = ApiClient.getClient().create(ApiService::class.java)

        var call = apiService.loginUser(email, password)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                loginResult!!.setValue(response.body())
            }

        })


        return loginResult!!
    }


}