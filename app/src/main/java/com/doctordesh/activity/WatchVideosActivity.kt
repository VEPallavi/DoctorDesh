package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.models.VideosModel
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_watch_videos.*

class WatchVideosActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {


        if (player == null) return

        if (!wasRestored) {
            player.cueVideo(video!!.video_id)
            player.play()
        }

    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

        Utils.showToast(this, resources.getString(R.string.msg_common_error))

        Handler().postDelayed(Runnable {
            finish()

        }, 900)

    }

    var video: VideosModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_videos)

        initView()


    }

    fun initView() {
        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setActionBar(toolbar)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.hasExtra("video")) {
            val type = object : TypeToken<VideosModel>() {}.type
            video = Gson().fromJson(intent.getStringExtra("video"), type)
        }




        toolbar.setNavigationOnClickListener { finish() }
        youtube_player_view.initialize(getString(R.string.youtube_api_key), this)


    }


}
