package com.doctordesh.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.activity.DocumentViewActivity
import com.doctordesh.helpers.AppPreference
import com.doctordesh.models.ChatListModel
import com.doctordesh.models.UserModel
import com.google.gson.Gson

class ChatListAdapter(var mContext: Context, var chatList: List<ChatListModel>) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    var preference: AppPreference? = null
    var user: UserModel? = null
    var dialog: Dialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {


        preference = AppPreference.getInstance(mContext)
        user = preference!!.getUserData()

        return ChatListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))

    }

    override fun getItemCount(): Int {


        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {

        holder.bind(chatList.get(position))

    }

    inner class ChatListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvUserMessage: TextView
        var tvMyMessage: TextView
        var ivUserImageMessage: ImageView
        var ivMyImageMessage: ImageView
        var pbDocFile: ProgressBar

        init {

            tvUserMessage = itemView.findViewById(R.id.tv_user_message)
            tvMyMessage = itemView.findViewById(R.id.tv_my_message)
            pbDocFile = itemView.findViewById(R.id.pb_doc_file)
            ivUserImageMessage = itemView.findViewById(R.id.iv_user_image_message)
            ivMyImageMessage = itemView.findViewById(R.id.iv_my_image_message)

            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (chatList.get(position).messageType.equals("2"))
                        openImageDialog(chatList.get(position).imageUrl)
                    else if (chatList.get(position).messageType.equals("3")) {

                        /*  mContext.startActivity(
                              Intent(mContext, DocumentViewActivity::class.java).putExtra(
                                  "doc",
                                  Gson().toJson(chatList.get(position)).toString()
                              )
                          )*/

                        var intent = Intent(Intent.ACTION_VIEW);

                        intent.setDataAndType(
                            Uri.parse("http://docs.google.com/viewer?url=" + chatList.get(position).imageUrl),
                            "text/html"
                        );

                        mContext.startActivity(intent)


                    }
                }

            })

        }

        fun bind(chat: ChatListModel) {

            if (chat.from.equals(user!!._id)) {

                /*SHOWING MY MESSAGE*/


                if (chat.messageType.equals("1")) {
                    tvMyMessage.text = chat.message
                    tvUserMessage.visibility = View.GONE
                    tvMyMessage.visibility = View.VISIBLE
                    ivUserImageMessage.visibility = View.GONE

                    ivMyImageMessage.visibility = View.GONE

                } else if (chat.messageType.equals("2")) {


                    Glide.with(mContext)
                        .load(chat.imageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                        .into(ivMyImageMessage)


                    tvUserMessage.visibility = View.GONE
                    tvMyMessage.visibility = View.GONE
                    ivUserImageMessage.visibility = View.GONE
                    ivMyImageMessage.visibility = View.VISIBLE

                } else if (chat.messageType.equals("3")) {


                    if (chat.isUploading) {
                        pbDocFile.visibility = View.VISIBLE


                    } else {
                        pbDocFile.visibility = View.GONE
                    }

                    var chatMessage = chat.message as String

                    var icon: Drawable? = null

                    if (chat.message.contains("txt")) {

                        chatMessage = chatMessage.replace("doc", "")

                        icon = mContext.resources.getDrawable(R.drawable.ic_txt)


                    } else if (chat.message.contains("doc")) {
                        chatMessage = chatMessage.replace("doc", "")
                        icon = mContext.resources.getDrawable(R.drawable.ic_txt)

                    } else if (chat.message.contains("pdf")) {
                        chatMessage = chatMessage.replace("pdf", "")
                        icon = mContext.resources.getDrawable(R.drawable.ic_pdf)

                    }

                    if (chatMessage.contains("."))
                        chatMessage = chatMessage.replace(".", "")






                    tvMyMessage.text = chatMessage
                    tvMyMessage.setCompoundDrawablesWithIntrinsicBounds(
                        icon,
                        null, null, null
                    )

                    tvUserMessage.visibility = View.GONE
                    tvMyMessage.visibility = View.VISIBLE
                    ivUserImageMessage.visibility = View.GONE
                    ivMyImageMessage.visibility = View.GONE


                }


            } else {

                /*SHOWING user MESSAGE*/


                tvUserMessage.text = chat.message
                tvUserMessage.visibility = View.VISIBLE
                tvMyMessage.visibility = View.GONE


                if (chat.messageType.equals("1")) {
                    tvUserMessage.text = chat.message
                    tvUserMessage.visibility = View.VISIBLE
                    tvMyMessage.visibility = View.GONE
                    ivUserImageMessage.visibility = View.GONE

                    ivMyImageMessage.visibility = View.GONE

                } else if (chat.messageType.equals("2")) {


                    Glide.with(mContext)
                        .load(chat.imageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                        .into(ivUserImageMessage)


                    tvUserMessage.visibility = View.GONE
                    tvMyMessage.visibility = View.GONE
                    ivUserImageMessage.visibility = View.VISIBLE
                    ivMyImageMessage.visibility = View.GONE

                } else if (chat.messageType.equals("3")) {


                    if (chat.isUploading) {
                        pbDocFile.visibility = View.VISIBLE
                    } else {
                        pbDocFile.visibility = View.GONE
                    }




                    if (chat.message.contains("txt")) {


                    } else if (chat.message.contains("doc")) {


                    } else if (chat.message.contains("pdf")) {

                    }

                    tvUserMessage.text = chat.message
                    tvMyMessage.visibility = View.GONE
                    tvUserMessage.visibility = View.VISIBLE
                    ivUserImageMessage.visibility = View.GONE
                    ivMyImageMessage.visibility = View.GONE


                }


            }
        }

    }


    fun openImageDialog(imagePath: String) {
        dialog = Dialog(mContext)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_image)
        dialog!!.show()
        dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        var ivImage = dialog!!.findViewById<ImageView>(R.id.iv_image)

        var ivCancel = dialog!!.findViewById<ImageView>(R.id.iv_cancel)

        ivCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialog!!.dismiss()
            }
        })
        Glide.with(mContext)
            .load(imagePath)
            .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
            .into(ivImage)


    }

}