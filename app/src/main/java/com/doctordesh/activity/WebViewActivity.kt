package com.doctordesh.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.network.ApiList
import com.google.firebase.firestore.util.ApiUtil

import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        initView()
    }

    fun initView() {


        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }





        var url = ""

        if (intent.getStringExtra("from").equals("about_us")) {
            url = ApiList.ABOUT_US_URL
            tv_header.text = getString(R.string.text_about_us)
            tv_zoom_link.visibility= View.GONE
        } else if (intent.getStringExtra("from").equals("terms_condition")) {
            url = ApiList.TERMS_CONDITION_URL
            tv_header.text = getString(R.string.text_terms_condition)
            tv_zoom_link.visibility= View.GONE
        }
        else if(intent.getStringExtra("from").equals("video_call"))
        {
            url = ApiList.VIDEO_CALL_INSTRUCTIONS_URL
            tv_header.text = getString(R.string.text_video_call_instructions)

            tv_zoom_link.visibility= View.VISIBLE
            tv_zoom_link.setOnClickListener(View.OnClickListener {

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


        }
        else if (intent.getStringExtra("from").equals("main_office"))
        {
            url=ApiList.MAIN_OFFICE_URL

            tv_header.text = getString(R.string.text_main_office)
            tv_zoom_link.visibility= View.GONE
        }





        Utils.showProgressDialog(this)
        webview.loadUrl(url)
        webview.setWebViewClient(CustomWebClient())
        webview.getSettings().setJavaScriptEnabled(true)


    }

    class CustomWebClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Utils.hideProgressDialog()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view!!.loadUrl(url)
            return true
        }


    }

}
