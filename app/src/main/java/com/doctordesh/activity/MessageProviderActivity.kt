package com.doctordesh.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.models.ProviderModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_message_provider.*

class MessageProviderActivity : AppCompatActivity() {

    var provider: ProviderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_provider)

        initView()

    }

    fun initView() {
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }


        if (intent != null && intent.hasExtra("provider")) {
            val type = object : TypeToken<ProviderModel>() {}.type

            provider = Gson().fromJson<ProviderModel>(intent.getStringExtra("provider"), type)


            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(this)
                .load(provider!!.profilePic)
                .apply(options)
                .into(profile_image)


            tv_name!!.text = provider!!.firstName + " " + provider!!.lastName
            btn_save.setOnClickListener(View.OnClickListener {

                openChatBeginDialog()

            })


        }


    }


    fun openChatBeginDialog()
    {

        var dialog =Dialog(this)

        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_before_chat_begins)
        dialog!!.show()
        dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.getWindow()!!.setGravity(Gravity.CENTER)


        var tvOk=dialog.findViewById<TextView>(R.id.tv_ok)
        tvOk.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, ChatActivity::class.java).putExtra("provider", Gson().toJson(provider)).putExtra("patient_detail", et_patient_detail.text.toString())
            )


            et_patient_detail.setText("")
        })




    }

}
