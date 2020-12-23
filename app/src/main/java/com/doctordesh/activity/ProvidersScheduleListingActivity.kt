package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.ProvidersListAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ProviderModel
import com.doctordesh.viewModels.ProvidersScheduleViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_providers_schedule_listing_screen.*
import kotlinx.android.synthetic.main.activity_providers_schedule_listing_screen.shimmer_view_container
import kotlinx.android.synthetic.main.activity_providers_schedule_listing_screen.toolbar
import kotlinx.android.synthetic.main.activity_providers_schedule_listing_screen.tv_no_provider
import kotlinx.android.synthetic.main.activity_staff_members_list.*


class ProvidersScheduleListingActivity : AppCompatActivity() {

    var providersScheduleViewModel: ProvidersScheduleViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers_schedule_listing_screen)
        initView()
    }

    fun initView() {

        providersScheduleViewModel =
            ViewModelProviders.of(this).get(ProvidersScheduleViewModel::class.java)



        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        shimmer_view_container.startShimmer()

        providersScheduleViewModel!!.getProvidersList(this).observe(this, Observer {


            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE


            if (it != null && it.has("status") && it.get("status").asString.equals("1")) {


                Utils.showLog("All Providers List : " + it.toString())



                val type = object : TypeToken<List<ProviderModel>>() {}.type

                var providersList =
                    Gson().fromJson<List<ProviderModel>>(it.get("payload").toString(), type)


                if (providersList != null && providersList.size > 0) {
                    rv_providers_list.setHasFixedSize(true)
                    rv_providers_list.layoutManager = LinearLayoutManager(this)
                    rv_providers_list.adapter = ProvidersListAdapter(this, providersList)
                    tv_no_provider.visibility= View.GONE

                } else {

                    rv_providers_list.visibility= View.GONE
                    tv_no_provider.visibility= View.VISIBLE
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
