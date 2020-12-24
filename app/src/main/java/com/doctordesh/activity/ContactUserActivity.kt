package com.doctordesh.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.ContactUserAdapter
import com.doctordesh.adapters.SurveyAdapter
import com.doctordesh.viewModels.ContactUserViewModel
import kotlinx.android.synthetic.main.activity_contact_user.*
import kotlinx.android.synthetic.main.activity_contact_user.toolbar
import kotlinx.android.synthetic.main.activity_meditation_wellness.*


class ContactUserActivity : AppCompatActivity(){
    var contactUserViewModel: ContactUserViewModel?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_user)
        initViews()
    }

    private fun initViews() {
        contactUserViewModel = ViewModelProviders.of(this).get(ContactUserViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val arrayList = ArrayList<String>()
        arrayList.add("conatct provider")
        arrayList.add("request medication")
        arrayList.add("send patient referal")
        arrayList.add("review provider schedule")
        arrayList.add("educational material")
        arrayList.add("evaluation survey")
        arrayList.add("meditation")
        arrayList.add("patient consent")
        arrayList.add("patient doc")
        arrayList.add("send patient doc")


        rv_contact_user_list.setHasFixedSize(true)
        rv_contact_user_list.layoutManager= LinearLayoutManager(this)
        rv_contact_user_list.adapter= ContactUserAdapter(this, arrayList)

    }


}