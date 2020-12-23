package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.doctordesh.R
import com.doctordesh.helpers.AppPreference
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    var preference: AppPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initView()
    }

    private fun initView() {


        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        preference = AppPreference.getInstance(this)


        toolbar.setNavigationOnClickListener { finish() }

        if (!preference!!.getNotificationStatus().equals("")) {
            sc_notification.isChecked = preference!!.getNotificationStatus().equals("1")
        } else {
            sc_notification.isChecked = true
        }

        sc_notification.setOnCheckedChangeListener { p0, value ->
            if (value) {
                preference!!.setNotificationStatus("1")
            } else {
                preference!!.setNotificationStatus("0")
            }
        }


        tv_terms_condition.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@SettingsActivity, WebViewActivity::class.java).putExtra("from", "terms_condition"))

            }

        })


    }

}
