package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.activity.ChatActivity
import com.doctordesh.activity.ContactUserActivity

class ContactUserAdapter(var mContext: Context, var contactUserDataList: ArrayList<String>) : RecyclerView.Adapter<ContactUserAdapter.ContactUserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactUserAdapter.ContactUserViewHolder {
        return ContactUserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_survey,parent,false))
    }

    override fun getItemCount(): Int {
        return contactUserDataList.size
    }

    override fun onBindViewHolder(holder: ContactUserAdapter.ContactUserViewHolder, position: Int) {

        holder.tvSurveyTitle.text = contactUserDataList.get(position)
        holder.iv_right_arrow.visibility = View.GONE

        holder.bindItems(contactUserDataList.get(position))
    }


    inner class ContactUserViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvSurveyTitle: TextView
        var clMainItem: ConstraintLayout
        var iv_right_arrow: ImageView

        init {
            tvSurveyTitle = view.findViewById(R.id.tv_survey_title)
            clMainItem = view.findViewById(R.id.cl_item)
            iv_right_arrow = view.findViewById(R.id.iv_right_arrow)
        }

        fun bindItems(dataModel: String){
            tvSurveyTitle.text = dataModel

            clMainItem.setOnClickListener {
                var contactUserDialog = Dialog(mContext)
                contactUserDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                contactUserDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                contactUserDialog!!.setContentView(R.layout.dialog_contact_user)
                contactUserDialog!!.show()
                contactUserDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                var clSendMessage = contactUserDialog.findViewById<ConstraintLayout>(R.id.cl_send_message)
                var clCallUser = contactUserDialog.findViewById<ConstraintLayout>(R.id.cl_call_user)

                contactUserDialog.show()

                clSendMessage.setOnClickListener {
                    contactUserDialog.dismiss()
                    mContext.startActivity(Intent(mContext, ChatActivity::class.java))
                }

                clCallUser.setOnClickListener {
                    var phoneNumber = "+917355228343"
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + phoneNumber)
                    mContext.startActivity(intent)
                }

            }
        }

    }

}