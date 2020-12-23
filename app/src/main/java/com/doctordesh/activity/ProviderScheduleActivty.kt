package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.adapters.ProviderTimingAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ProviderModel
import com.doctordesh.models.ProviderTimingModel
import com.doctordesh.viewModels.ProviderScheduleViewModel
import com.doctordesh.viewModels.ProvidersScheduleViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_provider_schedule_activty.*
import java.lang.Exception

class ProviderScheduleActivty : AppCompatActivity() {

    var provider: ProviderModel? = null
    var providerScheduleViewModel: ProviderScheduleViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_schedule_activty)
        initView()
    }

    fun initView() {


        providerScheduleViewModel =
            ViewModelProviders.of(this).get(ProviderScheduleViewModel::class.java)


        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        try {
            provider = intent.getParcelableExtra("provider")



            tv_provider_name.text = provider!!.firstName + " " + provider!!.lastName

            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            if (provider!!.profilePic != null) {
                Glide.with(this)
                    .load(provider?.profilePic)
                    .apply(options)
                    .into(profile_image)
            }


            shimmer_view_container.startShimmer()

            providerScheduleViewModel!!.getProviderSchedule(this, provider!!._id)
                .observe(this, Observer {


                    shimmer_view_container.stopShimmer()
                    shimmer_view_container.visibility = View.GONE



                    if (it != null && it.has("status") && it.get("status").asString.equals("1")) {
                        val type = object : TypeToken<List<ProviderTimingModel>>() {}.type
                        var providerScheduleList =
                            Gson().fromJson<List<ProviderTimingModel>>(
                                it.get("payload").toString(),
                                type
                            )


                        if (providerScheduleList != null && providerScheduleList.size > 0) {
                            tv_no_schedule.visibility = View.GONE
                            rv_provider_timing_list.visibility = View.VISIBLE

                            rv_provider_timing_list.setHasFixedSize(true)
                            rv_provider_timing_list.layoutManager = LinearLayoutManager(this)

                            rv_provider_timing_list.adapter =
                                ProviderTimingAdapter(this, providerScheduleList)


                        } else {
                            tv_no_schedule.visibility = View.VISIBLE
                            rv_provider_timing_list.visibility = View.GONE
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
        catch (e:Exception)
        {

            Utils.showToast(this,"Error in provider data")
        }


    }


}
