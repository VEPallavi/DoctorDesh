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
import retrofit2.http.FieldMap

class ProfileViewModel : ViewModel() {

    private var profileResult: MutableLiveData<JsonObject>? = null
    private var profilePicResult: MutableLiveData<JsonObject>? = null
    var preference: AppPreference? = null

    fun updateProfile(mContext: Context, firstName: String, lastName: String, phoneNumber: String, credentials: String, email: String,password: String, newPassword: String): MutableLiveData<JsonObject> {

        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()

        if (profileResult == null)
            profileResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)


        var params = HashMap<String, String>()
        if (!firstName.equals(""))
            params.put("firstName", firstName)
        if (!lastName.equals(""))
            params.put("lastName", lastName)
        if(!phoneNumber.equals(""))
            params.put("phoneNumber",phoneNumber)
        if (!credentials.equals(""))
            params.put("credentials", credentials)
        if (!email.equals(""))
            params.put("email", email)


        if(!password.equals("") && !newPassword.equals(""))
        {

            params.put("oldPassword",password)
            params.put("password",newPassword)


        }




        params.put("user_id", userModel!!._id)

        /*  @Field("firstName") firstName: String,
          @Field("lastName") lastName: String,
          @Field("credentials") credentials: String,
          @Field("email") email: String,
          @Field("device_token") deviceToken: String,
          @Field("user_id") userId: String*/


        var call = apiService.updateProfile(token, params)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                profileResult!!.setValue(response.body())
            }

        })


        return profileResult!!

    }

    fun updateProfilePic(mContext: Context, pic: MultipartBody.Part): MutableLiveData<JsonObject> {
        preference = AppPreference.getInstance(mContext)
        val userModel = preference!!.getUserData()

        val token = preference!!.getAuthToken()

        if (profilePicResult == null)
            profilePicResult = MutableLiveData()

        var apiService = ApiClient.getClient().create(ApiService::class.java)

        val id = RequestBody.create(MediaType.parse("multipart/form-data"), userModel!!._id)


        var call = apiService.updateProfilePic(token, id, pic)
        Utils.showProgressDialog(mContext)

        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utils.hideProgressDialog()
                Utils.showLog(t.message!!)
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Utils.hideProgressDialog()
                profilePicResult!!.setValue(response.body())
            }

        })



        return profilePicResult!!
    }


}