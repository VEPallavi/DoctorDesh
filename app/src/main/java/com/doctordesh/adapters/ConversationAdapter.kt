package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.activity.ChatActivity
import com.doctordesh.helpers.AppPreference
import com.doctordesh.interfaces.OnDeleteChatListener
import com.doctordesh.models.ChatListModel
import com.doctordesh.models.ChatUsersModel
import com.doctordesh.models.UserModel
import com.google.gson.Gson
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(var mContext: Context,var onDeleteChatListener: OnDeleteChatListener) :


    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    var prevDate: String=""
    var preference: AppPreference? = null
    var user: UserModel? = null
     var chatList: SortedList<ChatUsersModel>
    var sdfDay: SimpleDateFormat

    init {

        sdfDay =SimpleDateFormat("MMM dd, yyyy")
        chatList=SortedList(ChatUsersModel::class.java,object : SortedListAdapterCallback<ChatUsersModel>(this){
            override fun areItemsTheSame(item1: ChatUsersModel?, item2: ChatUsersModel?): Boolean {


                return item1!!.chatId == item2!!.chatId


            }

            override fun compare(o1: ChatUsersModel?, o2: ChatUsersModel?): Int {


                return (o2!!.chatTime.toLong() - o1!!.chatTime.toLong()).toInt()

            }

            override fun areContentsTheSame(
                oldItem: ChatUsersModel?,
                newItem: ChatUsersModel?
            ): Boolean {
               return oldItem == newItem

            }


        })





    }


fun updateChatItem(position: Int,chat: ChatUsersModel)
{
    chatList.updateItemAt(position,chat)
    notifyDataSetChanged()

}

    fun addChat(chat: ChatUsersModel)
    {

        chatList.add(chat)
        notifyDataSetChanged()

    }
    fun removeChatUser(chat: ChatUsersModel)
    {
        if(chatList.size()==0)
            return
        else
            chatList.remove(chat)

        notifyDataSetChanged()

    }



    override fun getItemCount(): Int {

        return chatList.size()
    }




    fun removeItem(position: Int)
    {
        chatList.remove(chatList.get(position))
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {

        if(!chatList.get(position).isHeaderChecked)
        {
            if(prevDate.equals(""))
            {
                prevDate= sdfDay.format(Date(chatList.get(position).chatTime.toLong()))
                chatList.get(position).isHeader=true
                holder.tvMsgDay.text=prevDate
                holder.tvMsgDay.visibility=View.VISIBLE

            }
            else
            {
               if(prevDate.equals(sdfDay.format(Date(chatList.get(position).chatTime.toLong()))))
               {
                   holder.tvMsgDay.visibility=View.GONE

               }
                else
               {
                   prevDate= sdfDay.format(Date(chatList.get(position).chatTime.toLong()))
                   chatList.get(position).isHeader=true

                   holder.tvMsgDay.text=prevDate
                   holder.tvMsgDay.visibility=View.VISIBLE
               }
            }

            chatList.get(position).isHeaderChecked=true
        }
        else
        {
            if(chatList.get(position).isHeader)
            {
                holder.tvMsgDay.visibility=View.VISIBLE
                holder.tvMsgDay.text=sdfDay.format(Date(chatList.get(position).chatTime.toLong()))
            }
            else
            {
                holder.tvMsgDay.visibility=View.GONE
            }
        }

        holder.bind(chatList.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {

        preference = AppPreference.getInstance(mContext)
        user = preference!!.getUserData()

        return ConversationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_conversation_list, parent, false))

    }

    inner class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var msgTime: TextView
        var userName: TextView
        var userMsg: TextView
        var tvMsgDay: TextView
        var ivMessageStatus: ImageView
        var profileImage: ImageView


        init {
            msgTime = view.findViewById(R.id.msg_time)
            userName = view.findViewById(R.id.user_name)
            userMsg = view.findViewById(R.id.user_msg)
            tvMsgDay = view.findViewById(R.id.tv_msg_day)
            profileImage = view.findViewById(R.id.profile_image)
            ivMessageStatus = view.findViewById(R.id.iv_message_status)
        }


        fun bind(chatUsersModel: ChatUsersModel) {
            var sdf = SimpleDateFormat("hh:mm a")
            msgTime.text = sdf.format(Date(chatUsersModel.chatTime.toLong()))


            if (chatUsersModel.messageType.equals("1"))
                userMsg.text = chatUsersModel.chatMessage
            else if (chatUsersModel.messageType.equals("2")) {

                userMsg.text = "Photo"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {


                    userMsg.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_camera_small,
                        0,
                        0,
                        0
                    )
                } else {
                    userMsg.setCompoundDrawables(
                        mContext.resources.getDrawable(R.drawable.ic_camera_small),
                        null,
                        null,
                        null
                    )

                }
            }

            if (chatUsersModel.isMessageRead.equals("0"))
                ivMessageStatus.visibility = View.VISIBLE
            else
                ivMessageStatus.visibility = View.GONE



            userName.text = chatUsersModel.name
            Glide.with(mContext)
                .load(chatUsersModel!!.pic)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(profileImage)

            /*    if (chatUsersModel.from.equals(user!!._id)) {
                    userMsg.text = chatUsersModel.chat_message
                    userName.text = chatUsersModel.name
                    Glide.with(mContext)
                        .load(chatUsersModel!!.my_pic)
                        .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                        .into(profileImage)
                } else {
                    userMsg.text = chatUsersModel.chat_message
                    userName.text = chatUsersModel.name
                    Glide.with(mContext)
                        .load(chatUsersModel!!.my_pic)
                        .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                        .into(profileImage)
                }*/



            itemView.setOnClickListener(View.OnClickListener {

                mContext.startActivity(
                    Intent(mContext, ChatActivity::class.java).putExtra(
                        "chatUser",
                        Gson().toJson(chatList.get(position))
                    )
                )
            })




            itemView.setOnLongClickListener {
             //   onDeleteChatListener.onDeleteChat(chatList.get(position), position)
                openDeleteChatDialog(position)
                true
            }


        }

    }


   fun openDeleteChatDialog(position: Int)
    {

        var dialog = Dialog(mContext)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_delete_chat)
        dialog!!.show()
        dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        var tvYes = dialog.findViewById<TextView>(R.id.tv_yes)
        tvYes.setOnClickListener(View.OnClickListener {
            onDeleteChatListener.onDeleteChat(chatList.get(position), position)
            dialog.dismiss()
        })


        var tvNo = dialog.findViewById<TextView>(R.id.tv_no)
        tvNo.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
        })

    }



}