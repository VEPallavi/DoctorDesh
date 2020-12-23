package com.doctordesh.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ChatListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_document_view.*

class DocumentViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_view)
        initView()
    }

    fun initView() {

        val type = object : TypeToken<ChatListModel>() {}.type

        var chat =
            Gson().fromJson<ChatListModel>(intent.getStringExtra("doc").toString(), type)

        /*   val doc =
               "<iframe src='http://docs.google.com/viewer?url=" + chat.imageUrl + "&embedded=true' width='100%' height='100%'  style='border: none;'></iframe>"
           Utils.showProgressDialog(this)
           wv_doc.setWebViewClient(CustomWebClient())
           wv_doc.getSettings().setJavaScriptEnabled(true)



           wv_doc.loadData(doc, "application/pdf", "UTF-8")*/


        wv_doc.getSettings().setJavaScriptEnabled(true)
       // Utils.showProgressDialog(this)
        wv_doc.setWebViewClient(CustomWebClient())
        wv_doc.loadUrl(chat.imageUrl)


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
