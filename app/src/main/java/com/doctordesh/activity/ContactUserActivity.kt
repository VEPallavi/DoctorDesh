package com.doctordesh.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.ContactUserAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.ContactUserItemList
import com.doctordesh.models.NotificationsItem
import com.doctordesh.viewModels.ContactUserViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_contact_user.*
import kotlinx.android.synthetic.main.activity_contact_user.toolbar



class ContactUserActivity : AppCompatActivity(){
    var viewModel: ContactUserViewModel?= null
    var contactUserItemList: ArrayList<ContactUserItemList>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_user)
        initViews()
    }

    private fun initViews() {
        viewModel = ViewModelProviders.of(this).get(ContactUserViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rv_contact_user_list.setHasFixedSize(true)
        rv_contact_user_list.layoutManager= LinearLayoutManager(this)
         getContactUserListData()

    }

    private fun getContactUserListData() {
        viewModel?.getData(this)?.observe(this, Observer {

            if(it!= null){
                if(it.has("status") && it.get("status").asString.equals("1")){

                    if(it.has("payload") &&  it.get("payload") is JsonArray){
                        val type = object : TypeToken<List<NotificationsItem>>() {}.type
                        contactUserItemList = Gson().fromJson<ArrayList<ContactUserItemList>>(it.get("payload").toString(), type)

                        if(contactUserItemList != null && contactUserItemList!!.size >0){
                            rv_contact_user_list.adapter= ContactUserAdapter(this, contactUserItemList!!)
                            rv_contact_user_list.visibility = View.VISIBLE
                            tv_no_data.visibility = View.GONE
                        }
                        else{
                            rv_contact_user_list.visibility = View.GONE
                            tv_no_data.visibility = View.VISIBLE
                        }
                    }
                    else {
                        Utils.showToast(this@ContactUserActivity, getString(R.string.msg_common_error))
                    }
                }
                else {
                    Utils.showToast(this@ContactUserActivity, getString(R.string.msg_common_error))
                }
            }
        })
    }


}