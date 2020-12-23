package com.doctordesh.activity

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.NotificationsAdapter
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.NotificationRecyclerSectionItem
import com.doctordesh.helpers.RecyclerSectionItemDecoration
import com.doctordesh.helpers.Utils
import com.doctordesh.models.NotificationsItem
import com.doctordesh.viewModels.NotificationListViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_notification_list.*
import java.text.SimpleDateFormat

class NotificationListActivity : AppCompatActivity(),
    NotificationRecyclerSectionItem.SectionCallback {

    override fun isSection(position: Int): Boolean {
        return position == 0 || !convertDate(notificationItemList!!.get(position).createdAt).equals(
            convertDate(notificationItemList!!.get(position - 1).createdAt)
        )

    }

    override fun getSectionHeader(position: Int): String {

        return convertDate(notificationItemList!!.get(position).createdAt)
    }


    fun convertDate(date: String): String {

        var sdfFrom = SimpleDateFormat("dd-MM-yyyy hh:mm")
        var sdfTo = SimpleDateFormat("dd MMM yyyy")


        var finalDate = sdfTo.format(sdfFrom.parse(date))
        return finalDate


    }


    var notificationListViewModel: NotificationListViewModel? = null
    var notificationItemList: List<NotificationsItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        initView()
    }

    private fun initView() {

        AppPreference.getInstance(this).setNotificationReadStatus(false)
        notificationListViewModel =
            ViewModelProviders.of(this).get(NotificationListViewModel::class.java)
        notificationListViewModel!!.getNotificationsList(this).observe(this, Observer {

            if (it != null) {

                if (it.has("status") && it.get("status").asString.equals("1")) {
                    if (it.has("payload") && it.get("payload") is JsonArray) {
                        val type = object : TypeToken<List<NotificationsItem>>() {}.type

                        notificationItemList = Gson().fromJson<List<NotificationsItem>>(
                            it.get("payload").toString(),
                            type
                        )

                        if (notificationItemList != null && notificationItemList!!.size > 0) {
                            rv_notification_list.adapter =
                                NotificationsAdapter(this, notificationItemList!!)
                            rv_notification_list.visibility = View.VISIBLE
                            tv_no_notifications.visibility = View.GONE
                        } else {

                            rv_notification_list.visibility = View.GONE
                            tv_no_notifications.visibility = View.VISIBLE

                        }


                    } else {
                        Utils.showToast(
                            this@NotificationListActivity,
                            getString(R.string.msg_common_error)
                        )
                    }
                } else {
                    Utils.showToast(
                        this@NotificationListActivity,
                        getString(R.string.msg_common_error)
                    )
                }


            }
        })

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        rv_notification_list.setHasFixedSize(true)

        rv_notification_list.layoutManager = LinearLayoutManager(this)
        /*  val sectionItemDecoration = NotificationRecyclerSectionItem(
              0,
              true,
              this
          )*/

        // rv_notification_list.addItemDecoration(sectionItemDecoration)

    }

}
