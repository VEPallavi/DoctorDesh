package com.doctordesh.firebaseServices

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.doctordesh.helpers.NotificationHelper
import com.doctordesh.helpers.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.lang.Exception
import android.app.ActivityManager
import android.content.Context
import com.doctordesh.activity.VideoCallInfoActivity
import com.doctordesh.helpers.AppPreference


class MessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        Utils.showLog(token!!)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Utils.showLog("From: " + remoteMessage!!.getFrom())
        try {

            val params = remoteMessage.data


            val messageObject = JSONObject(params as Map<*, *>)

            Utils.showLog("FCM notification: " + messageObject.toString())


            if (messageObject != null && messageObject.has("notification_type")) {


                val notificationType = messageObject.optString("notification_type")

                if (notificationType.equals("2")) {


                    val resultIntent = Intent(this, VideoCallInfoActivity::class.java)
                    resultIntent.putExtra("from", "notification")
                    resultIntent.putExtra("data", messageObject.toString())
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(resultIntent)

                } else if (notificationType.equals("3")) {

                    val i = Intent("UserBusy")

                    LocalBroadcastManager.getInstance(this).sendBroadcast(i)

                } else {
                    val title = messageObject.getString("title")
                    val message = messageObject.getString("message")
                    val url = messageObject.optString("image")

                    if (notificationType.equals("4") && isActivityRunning()) {
                    } else {

                        if (!notificationType.equals("4")) {
                            AppPreference.getInstance(this).setNotificationReadStatus(true)
                        }

                        var helper = NotificationHelper(this)
                        helper.createNotification(title, message, url, notificationType)
                    }
                }


                /*if (notificationType.equals("2")) {



                          var i = Intent(this, VideoChatActivity::class.java)
                          i.putExtra("from", "notification")
                          i.putExtra("data", messageObject.toString())
                          i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                          startActivity(i)


                } else if (notificationType.equals("3")) {

                    val i = Intent("UserBusy")

                    LocalBroadcastManager.getInstance(this).sendBroadcast(i)

                } else if (notificationType.equals("4")) {


                    helper.createNotification("test", "test", "", "4")

                }*/


            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isActivityRunning(): Boolean {

        val activityManager =
            this@MessagingService.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val activitys = activityManager.getRunningTasks(Integer.MAX_VALUE)
        var isActivityFound = false
        for (i in activitys.indices) {
            if (activitys[i].topActivity.toString().equals(
                    "ComponentInfo{com.doctordesh1/com.doctordesh.activity.ChatActivity}",
                    ignoreCase = true
                )
            ) {
                isActivityFound = true
            }
        }
        return isActivityFound
    }
}