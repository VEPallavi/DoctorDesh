package com.doctordesh.helpers

import android.R.attr.versionCode
import android.R.attr.versionName
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.doctordesh.BuildConfig
import com.doctordesh.R
import com.doctordesh.activity.LoginActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class Utils {
    companion object {

        var progressDialog: ProgressDialog? = null


        fun convertTimeIntoDate(time: String): String {
            var date = Date(time.toLong())
            var sdf = SimpleDateFormat("dd MMM yyyy")

            return sdf.format(date)

        }


        fun showProgressDialog(mContext: Context) {
            if (progressDialog != null && progressDialog!!.isShowing)
                progressDialog!!.dismiss()


            progressDialog = ProgressDialog(mContext)
            progressDialog!!.setMessage("Please wait")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()


        }

        fun hideProgressDialog() {
            if (progressDialog != null && progressDialog!!.isShowing)
                progressDialog!!.dismiss()

        }


        fun showTokeExpireDialog(mContext: Context) {
            var tokenExpiredDialog = Dialog(mContext)
            tokenExpiredDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            tokenExpiredDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            tokenExpiredDialog!!.setContentView(R.layout.dialog_alert_token_expired)
            tokenExpiredDialog!!.show()
            tokenExpiredDialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tokenExpiredDialog.setCancelable(false)
            tokenExpiredDialog.show()


            var tvOk = tokenExpiredDialog.findViewById<TextView>(R.id.tv_ok)


            tvOk.setOnClickListener {


                tokenExpiredDialog.dismiss()
                AppPreference.getInstance(mContext).setUserData("")
                var intent = Intent(mContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(intent)
                (mContext as Activity).finish()

            }


        }


        fun isInternetAvailable(mContext: Context?): Boolean {
            if (mContext != null) {
                val cm =
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                @SuppressLint("MissingPermission") val netInfo = cm.activeNetworkInfo
                return netInfo != null && netInfo.isConnectedOrConnecting
            } else
                return false
        }


        fun hideSoftKeyboard(activity: Context) {
            try {
                val inputMethodManager =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    (activity as Activity).currentFocus!!.windowToken,
                    0
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun isEmailValid(email: String): Boolean {
            var isValid = false

            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            if (matcher.matches()) {
                isValid = true
            }
            return isValid
        }


        fun showToast(mContext: Context, mesage: String) {
            Toast.makeText(mContext.applicationContext, mesage, Toast.LENGTH_SHORT).show()

        }

        fun showLog(message: String) {
            if (BuildConfig.DEBUG)
                Log.e("DoctorDesh", "Log message : " + message)

        }

        fun getAppVersion(mContext: Context): String {

            var response = ""

            try {
                val packageInfo: PackageInfo =
                    mContext.packageManager.getPackageInfo(mContext.packageName, 0)
                var versionName = packageInfo.versionName
                var versionCode = packageInfo.versionCode

                response = "Version : $versionName ($versionCode)"

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return response
        }



        fun getPsychometricSeverity(scaleName: String, totalScore: Int): String {
            var severity = ""

            when (scaleName) {
                "audit (alcohol screening)" -> {
                    when {
                        totalScore in 8..15 -> severity =
                                //"Advised to focus on reduction of hazardous drinking"
                            "Mild"
                        totalScore in 16..19 -> severity =
                                //"Brief counselling and continued monitoring"
                            "Moderate"
                        (totalScore > 20) -> severity =
                                //"Warrant further diagnostic evaluation for alcohol dependence"
                            "Severe"
                    }

                }
                "bims (cognitive screening)" -> {
                    when (totalScore) {
                        in 0..7 -> severity = "Severe impairment"
                        in 8..12 -> severity = "Moderately impaired"
                        in 13..15 -> severity = "Cognitively intact"
                    }
                }
                "bsds (bipolar screening)" -> {
                    when (totalScore) {
                        in 0..6 -> severity = "Highly unlikely probability of bipolar"
                        in 7..12 -> severity = "Low probability of bipolar"
                        in 13..19 -> severity = "Moderate probability of bipolar"
                        in 20..25 -> severity = "High probability of bipolar"
                    }
                }
                "btq (trauma screening)" -> {
                    when {
                        totalScore in 1..12 -> severity = "unlikely pseudobulbar affect"
                        totalScore in 13..16 -> severity = "possibility of pseudobulbar affect"
                        (totalScore > 17) -> severity = "likely pseudobulbar affect"
                    }
                }
                "cns_ls (pba screening)" -> {
                    when {
                        totalScore in 1..12 -> severity = "unlikely pseudobulbar affect"
                        totalScore in 13..16 -> severity = "possibility of pseudobulbar affect"
                        (totalScore > 17) -> severity = "likely pseudobulbar affect"
                    }
                }

                "dast (drug abuse screening)" -> {
                    when {
                        (totalScore == 0) -> severity = "Healthy"
                        totalScore in 1..2 -> severity = "Risky"
                        totalScore in 3..5 -> severity = "Harmful"
                        (totalScore > 6) -> severity = "Severe"
                    }
                }
                "faggerstorm (smoking screening)" -> {
                    when {
                        totalScore in 1..2 -> severity = "Low dependence"
                        totalScore in 3..4 -> severity = "Low to moderate dependence"
                        totalScore in 5..7 -> severity = "Moderate dependence"
                        (totalScore > 6) -> severity = "Severe dependence"
                    }
                }
                "gad7 (anxiety screening)" -> {
                    when (totalScore) {
                        in 0..5 -> severity = "Normal"
                        in 6..10 -> severity = "Mild anxiety"
                        in 11..15 -> severity = "Moderate anxiety"
                    }
                }
                "gds (geriatric depression screening)" -> {
                    when {
                        totalScore in 0..5 -> severity = "Not suggestive of depression"
                        totalScore in 6..10 -> severity =
                            "Suggestive of possible depression, need further assessment"
                        (totalScore > 10) -> severity =
                            "Almost always indicative of depression, most likely need treatment"
                    }
                }
                "pcl (ptsd screening)" -> {
                    when {
                        totalScore in 1..2 -> severity = "Below Moderately"
                        totalScore in 3..5 -> severity = "Moderately or above"
                        totalScore > 6 -> severity = "Moderately or above"

                    }
                }
                "phq9 (depression screening)" -> {
                    when (totalScore) {
                        in 0..4 -> severity = "Minimal depression"
                        in 5..9 -> severity = "Mild depression"
                        in 10..14 -> severity = "Moderate depression"
                        in 15..19 -> severity = "Moderately severe depression"
                        in 20..27 -> severity = "Severe depression"
                    }

                }
                "aims" -> {
                    when (totalScore) {
                        in 0..2 -> severity = "Mild"
                        3 -> severity = "Moderate"
                        4 -> severity = "Severe"
                    }

                }


                /*
                  switch score {
            case 0...2:
                return "Mild"
            case 3:
                return "Moderate"
            case 4:
                return "Severe"
            default:
                return ""
            }

                */


            }
            return severity;

        }







//        fun getPsychometricSeverity(scaleName: String, totalScore: Int): String {
//            var severity = ""
//
//            when (scaleName) {
//                "audit" -> {
//                    when {
//                        totalScore in 8..15 -> severity =
//                                //"Advised to focus on reduction of hazardous drinking"
//                            "Mild"
//                        totalScore in 16..19 -> severity =
//                                //"Brief counselling and continued monitoring"
//                            "Moderate"
//                        (totalScore > 20) -> severity =
//                                //"Warrant further diagnostic evaluation for alcohol dependence"
//                            "Severe"
//                    }
//
//                }
//                "bims and instructions" -> {
//                    when (totalScore) {
//                        in 0..7 -> severity = "Severe impairment"
//                        in 8..12 -> severity = "Moderately impaired"
//                        in 13..15 -> severity = "Cognitively intact"
//                    }
//                }
//                "bsds2" -> {
//                    when (totalScore) {
//                        in 0..6 -> severity = "Highly unlikely probability of bipolar"
//                        in 7..12 -> severity = "Low probability of bipolar"
//                        in 13..19 -> severity = "Moderate probability of bipolar"
//                        in 20..25 -> severity = "High probability of bipolar"
//                    }
//                }
//                "btq and scoring" -> ""
//                "cns_ls" -> when {
//
//                    totalScore in 1..12 -> severity = "unlikely pseudobulbar affect"
//                    totalScore in 13..16 -> severity = "possibility of pseudobulbar affect"
//                    (totalScore > 17) -> severity = "likely pseudobulbar affect"
//                }
//                "dast-english" -> {
//                    when {
//                        (totalScore == 0) -> severity = "Healthy"
//                        totalScore in 1..2 -> severity = "Risky"
//                        totalScore in 3..5 -> severity = "Harmful"
//                        (totalScore > 6) -> severity = "Severe"
//                    }
//                }
//                "fagerstrom_test" -> {
//                    when {
//                        totalScore in 1..2 -> severity = "Low dependence"
//                        totalScore in 3..4 -> severity = "Low to moderate dependence"
//                        totalScore in 5..7 -> severity = "Moderate dependence"
//                        (totalScore > 6) -> severity = "Severe dependence"
//                    }
//                }
//                "gad7" -> {
//                    when (totalScore) {
//                        in 0..5 -> severity = "Normal"
//                        in 6..10 -> severity = "Mild anxiety"
//                        in 11..15 -> severity = "Moderate anxiety"
//                    }
//                }
//                "gds" -> {
//                    when {
//                        totalScore in 0..5 -> severity = "Not suggestive of depression"
//                        totalScore in 6..10 -> severity =
//                            "Suggestive of possible depression, need further assessment"
//                        (totalScore > 10) -> severity =
//                            "Almost always indicative of depression, most likely need treatment"
//                    }
//                }
//                "pcl" -> {
//                    when {
//                        totalScore in 1..2 -> severity = "Below Moderately"
//                        totalScore in 3..5 -> severity = "Moderately or above"
//                        totalScore > 6 -> severity = "Moderately or above"
//
//                    }
//                }
//                "phq-9 scale" -> {
//                    when (totalScore) {
//                        in 0..4 -> severity = "Minimal depression"
//                        in 5..9 -> severity = "Mild depression"
//                        in 10..14 -> severity = "Moderate depression"
//                        in 15..19 -> severity = "Moderately severe depression"
//                        in 20..27 -> severity = "Severe depression"
//                    }
//
//                }
//                "aims" -> {
//                    when (totalScore) {
//                        in 0..2 -> severity = "Mild"
//                        3 -> severity = "Moderate"
//                        4 -> severity = "Severe"
//                    }
//
//                }
//
//
//                /*
//                  switch score {
//            case 0...2:
//                return "Mild"
//            case 3:
//                return "Moderate"
//            case 4:
//                return "Severe"
//            default:
//                return ""
//            }
//
//                */
//
//
//            }
//            return severity;
//
//        }


    }


}