package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.adapters.VideosAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.VideosModel
import com.doctordesh.network.ApiList
import com.doctordesh.viewModels.EducationalVideoViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_educational_video.*

class EducationalVideoActivity : AppCompatActivity() {


    var educationalVideoViewModel: EducationalVideoViewModel? = null
    var videoList: ArrayList<VideosModel> = ArrayList()
    var adapter: VideosAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_educational_video)
        initView()
    }

    fun initView() {


        educationalVideoViewModel = ViewModelProviders.of(this).get(EducationalVideoViewModel::class.java)

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }
        rv_videos_list.setHasFixedSize(true)
        rv_videos_list.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        adapter = VideosAdapter(this@EducationalVideoActivity, videoList)
        rv_videos_list.adapter = adapter

        var URL=""

        if(intent.getStringExtra("from").equals("PROVIDERS"))
        {
            URL=ApiList.GET_PROVIDERS_VIDEOS_URL
        }
        else
        {
            URL=ApiList.GET_VIDEOS_URL
        }





        educationalVideoViewModel!!.getVideoList(this,URL).observe(this, Observer {


            if (it != null) {
                if (it.has("status") && it.get("status").asString.equals("1")) {
                    if (it.has("payload") && it.get("payload") is JsonArray) {
                        val type = object : TypeToken<List<VideosModel>>() {}.type

                        var mVideoList = Gson().fromJson<List<VideosModel>>(it.get("payload").toString(), type)
                        videoList.addAll(mVideoList)
                        adapter!!.notifyDataSetChanged()

                    }
                }


            }


        })


    }


}
