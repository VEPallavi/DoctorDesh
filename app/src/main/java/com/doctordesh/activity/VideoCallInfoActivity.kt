package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.doctordesh.R
import kotlinx.android.synthetic.main.activity_video_call_info.*
import android.content.Intent



import android.net.Uri


class VideoCallInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_info)
        tv_link.setOnClickListener(View.OnClickListener {

            var intent = packageManager.getLaunchIntentForPackage("us.zoom.videomeetings")
            if (intent != null) {
                // We found the activity now start the activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // Bring user to the market or let them choose an app?
                intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=" + "us.zoom.videomeetings")
                startActivity(intent)
            }


        })

        iv_cancel.setOnClickListener(View.OnClickListener {

            finish()

        })


    }
}
