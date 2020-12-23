package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.StaffListAdapter
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ProviderModel

import com.doctordesh.viewModels.StaffMemberListViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_staff_members_list.*
import kotlinx.android.synthetic.main.activity_staff_members_list.toolbar

class StaffMembersListActivity : AppCompatActivity() {

    var staffMemberListViewModel: StaffMemberListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_members_list)
        initView()
    }

    fun initView() {

        staffMemberListViewModel =
            ViewModelProviders.of(this).get(StaffMemberListViewModel::class.java)




        toolbar.title = ""


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }



        if (intent.getStringExtra("provider_type") == "1") {
            tv_title.text = resources.getString(R.string.text_staff_call_provider)
        } else {
            tv_title.text = resources.getString(R.string.text_staff_message_provider)
        }


        shimmer_view_container.startShimmer()

        staffMemberListViewModel!!.getStaffMemberList(this, intent.getStringExtra("provider_type"))
            .observe(this, Observer {


                shimmer_view_container.stopShimmer()
                shimmer_view_container.visibility = View.GONE

                if (it != null && it.has("status") && it.get("status").asString.equals("1")) {


                    val type = object : TypeToken<List<ProviderModel>>() {}.type

                    var providersList =
                        Gson().fromJson<List<ProviderModel>>(it.get("payload").toString(), type)



                    if (providersList != null && providersList.isNotEmpty()) {

                        providersList = providersList.filter { item ->
                            (item._id != AppPreference.getInstance(this).getUserData()!!._id)
                        }


                        rv_staff_members.setHasFixedSize(true)
                        rv_staff_members.layoutManager = LinearLayoutManager(this)
                        rv_staff_members.adapter = StaffListAdapter(
                            this,
                            providersList,
                            intent.getStringExtra("provider_type")
                        )
                        tv_no_provider.visibility = View.GONE

                    } else {

                        rv_staff_members.visibility = View.GONE
                        tv_no_provider.visibility = View.VISIBLE
                    }


                } else {
                    var message = ""
                    if (it.has("message")) {
                        message = it.get("message").asString
                    } else {
                        message = getString(R.string.msg_common_error)
                    }

                    Utils.showToast(this, message)
                }

            })


    }
}
