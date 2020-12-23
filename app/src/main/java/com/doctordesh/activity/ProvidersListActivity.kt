package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.ProvidersAdapter
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ProviderModel
import com.doctordesh.viewModels.ProvidersListViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_providers_list.*

class ProvidersListActivity : AppCompatActivity() {

    var providersListViewModel: ProvidersListViewModel? = null
    var PROVIDERS_TYPE = 1
    //////////////////////////
    //////PROVIDERS TYPE//////
    // 1 : CALL PROVIDER
    // 2 : MESSAGE PROVIDER
    // 3 : VIDEO CALL PROVIDER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers_list)
        initView()
    }

    private fun initView() {

        providersListViewModel = ViewModelProviders.of(this).get(ProvidersListViewModel::class.java)

        if (intent.hasExtra("provider_type")) {
            PROVIDERS_TYPE = intent.getIntExtra("provider_type", 1)
        }


        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }


        if (PROVIDERS_TYPE == 1)
            tv_title.text = resources.getString(R.string.text_call_provider)
        else if (PROVIDERS_TYPE == 2)
            tv_title.text = resources.getString(R.string.text_message_provider)
        else
            tv_title.text = resources.getString(R.string.text_video_call_provider)



        providersListViewModel!!.getProvidersList(this, PROVIDERS_TYPE.toString())
            .observe(this, Observer {

                if (it != null) {
                    if (it.has("status")) {
                        if (it.get("status").asString.equals("1") && it.has("payload") && it.get("payload") is JsonArray) {


                            val type = object : TypeToken<List<ProviderModel>>() {}.type

                            var providersList = Gson().fromJson<List<ProviderModel>>(
                                it.get("payload").toString(),
                                type
                            )

                            if (providersList != null && providersList.isNotEmpty())
                            {
                                providersList= providersList.filter { item ->
                                    (item._id != AppPreference.getInstance(this).getUserData()!!._id)
                                }

                                rv_providers_list.adapter =
                                    ProvidersAdapter(this, PROVIDERS_TYPE, providersList)
                            }





                        } else {
                            if (it.has("message"))
                                Utils.showToast(this, it.get("message").asString)
                            else {
                                Utils.showToast(
                                    this,
                                    resources.getString(R.string.msg_common_error)
                                )
                            }
                        }


                    } else {
                        Utils.showToast(this, resources.getString(R.string.msg_common_error))
                    }
                }


            })


        rv_providers_list.setHasFixedSize(true)
        rv_providers_list.layoutManager = LinearLayoutManager(this)


    }
}
