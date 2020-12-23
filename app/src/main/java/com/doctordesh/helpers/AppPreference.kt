package com.doctordesh.helpers

import android.content.Context
import android.content.SharedPreferences
import com.doctordesh.models.CredentialModel
import com.doctordesh.models.UserModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.Exception

class AppPreference() {

    var mContext: Context? = null
    var APP_PREFERENCE = "APP_PREFERENCE"
    var USER_DATA = "USER_DATA"
    var DEVICE_TOKEN = "DEVICE_TOKEN"
    var TOKEN = "TOKEN"
    var NOTIFICATION_STATUS = "NOTIFICATION_STATUS"
    var mPreference: SharedPreferences? = null
    var CREDENTIALS="CREDENTIALS"

    var NOTIFICATION_READ_STATUS = "NOTIFICATION_READ_STATUS"



    constructor(mContext: Context) : this() {
        this.mContext = mContext
        mPreference = mContext.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)


    }

    companion object {
        var instance: AppPreference? = null


        fun getInstance(mContext: Context): AppPreference {

            if (instance == null)
                instance = AppPreference(mContext)

            return instance as AppPreference


        }


    }


    fun setCredentials(credentials: String)
    {

        setString(CREDENTIALS,credentials)
    }


    fun getCredentials(): ArrayList<CredentialModel>
    {

        var credentialsList = ArrayList<CredentialModel>()


        try {
            val type = object : TypeToken<ArrayList<CredentialModel>>() {}.type

             credentialsList = Gson().fromJson<ArrayList<CredentialModel>>(getString(CREDENTIALS), type)

            return credentialsList
        } catch (e: Exception) {
            return credentialsList
        }


    }


    fun getUserData(): UserModel? {
        try {
            val type = object : TypeToken<UserModel>() {}.type

            var user = Gson().fromJson<UserModel>(getString(USER_DATA), type)

            return user
        } catch (e: Exception) {
            return null
        }

    }

    fun setUserData(userData: String) {
        setString(USER_DATA, userData)


    }



    fun getNotificationReadStatus(): Boolean {
        return getBoolean(NOTIFICATION_READ_STATUS)
    }


    fun setNotificationReadStatus(value: Boolean) {
        setBoolean(NOTIFICATION_READ_STATUS, value)
    }




    fun getNotificationStatus(): String {
        return getString(NOTIFICATION_STATUS)
    }


    fun setNotificationStatus(token: String) {
        setString(NOTIFICATION_STATUS, token)
    }


    fun getDeviceToken(): String {
        return getString(DEVICE_TOKEN)
    }


    fun setDeviceToken(token: String) {
        setString(DEVICE_TOKEN, token)
    }


    fun getAuthToken(): String {
        return getString(TOKEN)
    }


    fun setAuthToken(token: String) {
        setString(TOKEN, token)
    }


    fun setString(key: String, value: String) {
        val editor = mPreference!!.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun getString(key: String): String {
        return mPreference!!.getString(key, "").toString()
    }


    fun setBoolean(key: String, value: Boolean) {
        val editor = mPreference!!.edit()
        editor.putBoolean(key, value)
        editor.apply()


    }

    fun getBoolean(key: String): Boolean {
        return mPreference!!.getBoolean(key, false)
    }


}