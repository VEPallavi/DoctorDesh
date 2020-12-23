package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.PsychMaterialAdapter
import com.doctordesh.models.PsychMaterialModel
import com.doctordesh.network.ApiClient
import com.doctordesh.network.ApiList
import com.doctordesh.viewModels.PsychMaterialViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_psych_material.*

class PsychMaterialActivity : AppCompatActivity() {


    var psychMaterialViewModel: PsychMaterialViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psych_material)
        init()
    }

    fun init() {

        psychMaterialViewModel=ViewModelProviders.of(this).get(PsychMaterialViewModel::class.java)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }



        tv_title.text = intent.getStringExtra("from")


        var URL = ""

        if (intent.getStringExtra("from").equals(getString(R.string.text_learn_psych_meds))) {
            URL = ApiList.GET_PSYCH_MED_URL
        } else {
            URL = ApiList.GET_PSYCH_DISORDERS_URL
        }


        psychMaterialViewModel!!.getPsychMaterial(this, URL).observe(this, Observer {


            if (it != null) {
                if (it.has("status") && it.get("status").asString.equals("1")) {
                    if (it.has("payload") && it.get("payload") is JsonArray) {
                        val type = object : TypeToken<List<PsychMaterialModel>>() {}.type

                        var dataList = Gson().fromJson<List<PsychMaterialModel>>(
                            it.get("payload").toString(),
                            type
                        )


                        if(dataList!=null && dataList.size>0)
                        {
                            rv_psych_material.setHasFixedSize(true)
                            rv_psych_material.layoutManager=LinearLayoutManager(this)
                            rv_psych_material.adapter=PsychMaterialAdapter(this,dataList)


                            rv_psych_material.visibility= View.VISIBLE
                            tv_no_psych_material.visibility=View.GONE


                        }
                        else
                        {
                            tv_no_psych_material.visibility= View.VISIBLE
                            rv_psych_material.visibility=View.GONE


                        }


                    }
                }


            }


        })


    }
}
