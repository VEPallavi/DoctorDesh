package com.doctordesh.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.doctordesh.R
import com.doctordesh.activity.*
import com.doctordesh.models.UserModel

import java.io.IOException
import java.io.InputStream
import java.net.URL

class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    /**
     * Create and push the notification
     */
    fun createNotification(title: String, message: String, image: String?, notificationType: String) {
        /**Creates an explicit intent for an Activity in your app */


        var resultIntent: Intent? = null
        var user = AppPreference.getInstance(mContext).getUserData() as UserModel


        if (notificationType.equals("4", ignoreCase = true)) {


            if (AppPreference.getInstance(mContext).getUserData() != null && !AppPreference.getInstance(mContext).getUserData()!!.equals(""))
            {
                resultIntent = Intent(mContext, SplashActivity::class.java)
            }
            else
            {
                if (user.type.equals("1")) {
                    resultIntent = Intent(mContext, ProvidersDashboardActivity::class.java)
                } else {
                    resultIntent = Intent(mContext, ConversationActivity::class.java)
                }


            }




        } else {

            if (AppPreference.getInstance(mContext).getUserData() != null && !AppPreference.getInstance(mContext).getUserData()!!.equals(""))
            {
                resultIntent = Intent(mContext, SplashActivity::class.java)
            }
            else
            {
                if (user.type.equals("1")) {
                    resultIntent = Intent(mContext, ProvidersDashboardActivity::class.java)
                } else {
                    resultIntent = Intent(mContext, DashboardActivity::class.java)
                }


            }
        }


        resultIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val rawBitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.app_icon)

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var bmp: Bitmap? = null
        if (image != null && !image.equals("", ignoreCase = true)) {

            try {
                val `in` = URL(image).openStream()
                bmp = BitmapFactory.decodeStream(`in`)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        mBuilder = NotificationCompat.Builder(mContext)
        mBuilder!!.setSmallIcon(R.drawable.ic_notifications_white)
            .setLargeIcon(rawBitmap)

        mBuilder!!.setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)

            .setAutoCancel(true)

        if (bmp != null) {
            mBuilder!!.setLargeIcon(bmp)
            mBuilder!!.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp))
        }
        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 100, 100, 100)
            assert(mNotificationManager != null)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(0 /* Request Code */, mBuilder!!.build())
    }

    companion object {
        val NOTIFICATION_CHANNEL_ID = "10001"
    }
}
