package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.helpers.Utils
import com.doctordesh.models.NotificationsItem

import java.text.SimpleDateFormat


class NotificationsAdapter(var mContext: Context, var notificationsList: List<NotificationsItem>) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>() {

    var sdfTo: SimpleDateFormat? = null
    var sdfFrom: SimpleDateFormat? = null
    var sdfDate: SimpleDateFormat? = null
    var prevDate = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {

        sdfTo = SimpleDateFormat("hh:mm a")
        sdfDate = SimpleDateFormat("dd MMM yyyy")
        sdfFrom = SimpleDateFormat("dd-MM-yyyy hh:mm")


        return NotificationsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notification,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
//        holder.bind(notificationsList.get(position))


        var item = notificationsList.get(position)

        holder.tvNotificationTitle.text = item.title
        holder.tvNotificationDesc.text = item.content

        var date = sdfFrom!!.parse(item.createdAt)


        var notificationDate = sdfDate!!.format(date)



        if (!item.headerSelected) {
            Utils.showLog("Notification Date : " + notificationDate + " Prev Date : " + prevDate)

            if (!prevDate.equals(notificationDate)) {
                holder.tvNotificationDate.text = notificationDate
                holder.tvNotificationDate.visibility = View.VISIBLE
                notificationsList.get(position).isHeader = true
                prevDate = notificationDate
            } else {
                holder.tvNotificationDate.visibility = View.GONE
            }


            notificationsList.get(position).headerSelected = true

        } else {
            if (item.isHeader) {
                holder.tvNotificationDate.text = notificationDate
                holder.tvNotificationDate.visibility = View.VISIBLE

            } else {
                holder.tvNotificationDate.text = notificationDate
                holder.tvNotificationDate.visibility = View.GONE
            }


        }



        holder.tvNotificationTime.text = sdfTo!!.format(date)






        if (!item.notify_image.equals("")) {
            Glide.with(mContext)
                .load(item!!.notify_image)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.ivNotificationImage)

            holder.ivNotificationImage.visibility = View.VISIBLE
        } else {
            holder.ivNotificationImage.visibility = View.GONE
        }


    }

    inner class NotificationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivNotificationImage: ImageView
        var tvNotificationDesc: TextView
        var tvNotificationTitle: TextView
        var tvNotificationTime: TextView


        var tvNotificationDate: TextView


        init {

            ivNotificationImage = view.findViewById(R.id.iv_notification_image)
            tvNotificationDesc = view.findViewById(R.id.tv_notification_desc)
            tvNotificationTitle = view.findViewById(R.id.tv_notification_title)
            tvNotificationTime = view.findViewById(R.id.tv_notification_time)
            tvNotificationDate = view.findViewById(R.id.tv_notification_date)


            itemView.setOnClickListener(View.OnClickListener {

                openDialog(notificationsList.get(adapterPosition))

            })


        }










        fun openDialog( notificationsItem:NotificationsItem)
        {

            var dialog = Dialog(mContext)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
          //  dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setContentView(R.layout.dialog_notification)
            dialog!!.show()
            dialog!!.window!!.setGravity(Gravity.CENTER)
            dialog!!.getWindow()!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            var tvHeader=dialog.findViewById<TextView>(R.id.tv_header)


            var tvMsg=dialog.findViewById<TextView>(R.id.tv_msg)

            var ivImage=dialog.findViewById<ImageView>(R.id.iv_image)



            tvHeader.text=notificationsItem.title
            tvMsg.text=notificationsItem.content
            if(notificationsItem.notify_image!=null && !notificationsItem.notify_image.equals(""))
            {
                ivImage.visibility=View.VISIBLE

                Glide.with(mContext)
                    .load(notificationsItem.notify_image)
                    .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                    .into(ivImage)
            }
            else
            {
                ivImage.visibility=View.GONE
            }




            var ivCancel=dialog.findViewById<ImageView>(R.id.iv_cancel)
            ivCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })


        }



    }
}