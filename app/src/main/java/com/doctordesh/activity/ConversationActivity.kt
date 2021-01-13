package com.doctordesh.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SortedList
import com.doctordesh.R
import com.doctordesh.adapters.ConversationAdapter
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.RecyclerSectionItemDecoration
import com.doctordesh.helpers.Utils
import com.doctordesh.interfaces.OnDeleteChatListener
import com.doctordesh.models.ChatListModel
import com.doctordesh.models.ChatUsersModel
import com.doctordesh.models.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_conversation.*
import java.util.*
import kotlin.collections.ArrayList

class ConversationActivity : AppCompatActivity(), RecyclerSectionItemDecoration.SectionCallback,
    OnDeleteChatListener {
    override fun onDeleteChat(chatUser: ChatUsersModel, pos: Int) {
        mDatabase.child(chatUser.providerId!!).removeValue()

        conversationAdpter!!.removeItem(pos)
        deleteChatMessages(chatUser)

    }


    override fun isSection(position: Int): Boolean {
        if (position == 0 || !Utils.convertTimeIntoDate(chatList.get(position).chatTime).equals(
                Utils.convertTimeIntoDate(chatList.get(position - 1).chatTime)
            )
        ) {
            return true
        } else
            return false
    }

    override fun getSectionHeader(position: Int): String {
        return Utils.convertTimeIntoDate(chatList.get(position).chatTime)
    }


    val db = FirebaseFirestore.getInstance()
    var preference: AppPreference? = null
    var user: UserModel? = null
    var chatList = ArrayList<ChatUsersModel>()
    var conversationAdpter: ConversationAdapter? = null
    lateinit var mDatabase: DatabaseReference
    var valueEventListener: ValueEventListener? = null
    var childEventListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        initView()
        NotificationManagerCompat.from(this).cancel(0)

    }

    private fun initView() {
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        preference = AppPreference.getInstance(this)
        user = preference!!.getUserData()



        rv_chat_list.setHasFixedSize(true)
        rv_chat_list.layoutManager = LinearLayoutManager(this)
        conversationAdpter = ConversationAdapter(this, this)
        rv_chat_list.adapter = conversationAdpter



        getChatList()

    }

    private fun getChatList() {

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user!!._id)
        valueEventListener = mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Utils.hideProgressDialog()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("Count ", "" + snapshot.getChildrenCount())
                if (snapshot.getChildrenCount() > 0) {
                    rv_chat_list.visibility = View.VISIBLE
                    tv_no_chat_found.visibility = View.GONE
                } else {
                    rv_chat_list.visibility = View.GONE
                    tv_no_chat_found.visibility = View.VISIBLE
                }

            }


        })
        childEventListener = mDatabase.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

                Log.e("CallMe4", "onChildAdded called")
                try {

                    val chatId = dataSnapshot.child("chatId").getValue(String::class.java)!!
                    val chatMessage =
                        dataSnapshot.child("chatMessage").getValue(String::class.java)!!
                    val chatTime = dataSnapshot.child("chatTime").getValue(String::class.java)!!
                    val isMessageRead =
                        dataSnapshot.child("messageRead").getValue(String::class.java)!!
                    val senderPic = dataSnapshot.child("pic").getValue(String::class.java)?.let { it }

                    val messageType =
                        dataSnapshot.child("messageType").getValue(String::class.java)!!
                    val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java)!!
                    val providerId = dataSnapshot.child("providerId").getValue(String::class.java)!!
                    val userId = dataSnapshot.child("userId").getValue(String::class.java)!!
                    val senderName = dataSnapshot.child("name").getValue(String::class.java)!!
                    val deviceToken =
                        dataSnapshot.child("deviceToken").getValue(String::class.java)!!

                    var chatUsersModel = ChatUsersModel(
                        chatId,
                        chatMessage,
                        chatTime,
                        isMessageRead,
                        messageType,
                        imageUrl,
                        senderName,
                        senderPic,
                        false, false, userId,
                        providerId,
                        deviceToken
                    )




                    for (i in 0..chatList.size - 1) {
                        if (chatList.get(i).chatId.equals(chatUsersModel.chatId)) {
                            chatList.set(i, chatUsersModel)
                            conversationAdpter!!.updateChatItem(i, chatUsersModel)
                            break
                        }
                    }

                } catch (e: Exception) {
                    Log.e("CallMe4", "catch called")
                    e.printStackTrace()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                Log.e("CallMe4", "onChildAdded called")
                try {

                    val chatId = dataSnapshot.child("chatId").getValue(String::class.java)!!
                    val chatMessage =
                        dataSnapshot.child("chatMessage").getValue(String::class.java)!!
                    val chatTime = dataSnapshot.child("chatTime").getValue(String::class.java)!!
                    val isMessageRead =
                        dataSnapshot.child("messageRead").getValue(String::class.java)!!
                    val senderPic = dataSnapshot.child("pic").getValue(String::class.java)?.let { it }

                    val messageType =
                        dataSnapshot.child("messageType").getValue(String::class.java)!!
                    val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java)!!
                    val providerId = dataSnapshot.child("providerId").getValue(String::class.java)!!
                    val userId = dataSnapshot.child("userId").getValue(String::class.java)!!
                    val senderName = dataSnapshot.child("name").getValue(String::class.java)!!


                    val deviceToken =
                        dataSnapshot.child("deviceToken").getValue(String::class.java)!!

                    var chatUsersModel = ChatUsersModel(
                        chatId,
                        chatMessage,
                        chatTime,
                        isMessageRead,
                        messageType,
                        imageUrl,
                        senderName,
                        senderPic,
                        false, false, userId,
                        providerId,
                        deviceToken
                    )




                    conversationAdpter!!.addChat(chatUsersModel)

                    //addItemInSortedOrder(chatUsersModel)

                    // pconversationAdpter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("CallMe4", "catch called")
                    e.printStackTrace()
                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()

        if (valueEventListener != null)
            mDatabase.removeEventListener(valueEventListener!!)
    }


    fun deleteChatMessages(chatUser: ChatUsersModel) {

        var mChatDatabase = FirebaseDatabase.getInstance().getReference("messages")

        mChatDatabase.child(chatUser!!.chatId!!).removeValue()

        Utils.showToast(this, "Conversation deleted successfully.")

    }


}
