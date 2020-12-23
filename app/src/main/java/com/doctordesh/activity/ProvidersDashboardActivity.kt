package com.doctordesh.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.UserModel
import com.doctordesh.viewModels.ProvidersDashboardViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.adapters.ConversationAdapter
import com.doctordesh.helpers.RecyclerSectionItemDecoration
import com.doctordesh.interfaces.OnDeleteChatListener
import com.doctordesh.models.ChatUsersModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_providers_dashboard.*
import java.util.ArrayList
import com.google.firebase.database.DataSnapshot
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager


class ProvidersDashboardActivity : AppCompatActivity(), View.OnClickListener {



    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cv_provider -> {
                startActivity(Intent(this, ContactProviderActivity::class.java))
            }
            R.id.cv_staff -> {
                startActivity(Intent(this, StaffMemberActivity::class.java))
            }
            R.id.cv_main_office -> {
                startActivity(Intent(this, WebViewActivity::class.java).putExtra("from","main_office"))
            }
        }
    }





    private fun initView() {



        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        cv_provider.setOnClickListener(this)
        cv_staff.setOnClickListener(this)
        cv_main_office.setOnClickListener(this)


        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_provider.visibility = View.VISIBLE

        }, 500)

        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_staff.visibility = View.VISIBLE

        }, 600)
        Handler().postDelayed(Runnable {
            var transition = Slide(Gravity.BOTTOM)
            transition.duration = 900
            TransitionManager.beginDelayedTransition(cl_main, transition)
            cv_main_office.visibility = View.VISIBLE

        }, 700)





    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers_dashboard)

        initView()



    }





}
