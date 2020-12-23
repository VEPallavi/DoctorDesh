package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.AssesmentsAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.PsychCategoryModel
import com.doctordesh.viewModels.PsychometricScaleViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_assesment.*

class PsychometricScaleActivity : AppCompatActivity() {

    var psychometricScaleViewModel: PsychometricScaleViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assesment)


        initView()
    }

    fun initView() {

        psychometricScaleViewModel =
            ViewModelProviders.of(this).get(PsychometricScaleViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        shimmer_view_container.startShimmer()

        psychometricScaleViewModel!!.getPsychometricScales(this).observe(this, Observer {


            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE

            Utils.showLog("Psych categories response : " + it.toString())

            if (it != null && it.has("status") && it.get("status").asString.equals("1")) {
                val type = object : TypeToken<List<PsychCategoryModel>>() {}.type
                var psychCategoriesList =
                    Gson().fromJson<List<PsychCategoryModel>>(
                        it.get("payload").toString(),
                        type
                    )

                if (psychCategoriesList != null && psychCategoriesList.size > 0) {
                    rv_assesment_list.setHasFixedSize(true)
                    rv_assesment_list.layoutManager = LinearLayoutManager(this)
                    rv_assesment_list.adapter = AssesmentsAdapter(this, psychCategoriesList)

                    tv_no_assesments.visibility = View.GONE
                    rv_assesment_list.visibility = View.VISIBLE

                } else {
                    tv_no_assesments.visibility = View.VISIBLE
                    rv_assesment_list.visibility = View.GONE
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
