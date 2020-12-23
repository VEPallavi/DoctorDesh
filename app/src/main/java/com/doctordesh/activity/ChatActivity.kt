package com.doctordesh.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.LayoutDirection
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.adapters.ChatListAdapter
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.adapters.ImageChatMessageAdapter
import com.doctordesh.helpers.RealPathUtil
import com.doctordesh.helpers.RecyclerSectionItemDecoration
import com.doctordesh.models.*
import com.doctordesh.viewModels.ChatViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import droidninja.filepicker.FilePickerBuilder
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity(), View.OnClickListener,
    ImageChatMessageAdapter.OnDeleteImage, RecyclerSectionItemDecoration.SectionCallback {


    override fun isSection(position: Int): Boolean {

        if (position == 0 || !Utils.convertTimeIntoDate(chatMessages.get(position).messageTime).equals(
                Utils.convertTimeIntoDate(chatMessages.get(position - 1).messageTime)
            )
        ) {
            return true
        } else
            return false

    }


    override fun getSectionHeader(position: Int): String {


        return Utils.convertTimeIntoDate(chatMessages.get(position).messageTime)
    }

    override fun onDelete(position: Int) {

        imagesChatMessages.removeAt(position)
        imageChatMessageAdapter!!.notifyItemRemoved(position)


        if (imagesChatMessages.size == 0)
            rv_images_list.visibility = View.GONE


    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.iv_add_image -> {


                if (checkPermission())
                    openAttachmentDialog()
                else {
                    requestPermission()
                }


            }
        }
    }

    fun openAttachmentDialog() {
        if (attachmentDialog != null && attachmentDialog!!.isShowing) {
            attachmentDialog!!.dismiss()
        }

        attachmentDialog = Dialog(this, R.style.DialogSlideAnimation)
        attachmentDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        attachmentDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        attachmentDialog!!.setContentView(R.layout.dialog_attachment_items)
        attachmentDialog!!.getWindow()!!.setGravity(Gravity.BOTTOM)
        attachmentDialog!!.show()
        attachmentDialog!!.getWindow()!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        var clDocument = attachmentDialog!!.findViewById<ConstraintLayout>(R.id.cl_document)

        clDocument.setOnClickListener(View.OnClickListener {

            attachmentDialog!!.dismiss()


            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setType("*/*")

            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, PICK_DOCUMENT_CODE)

            /* var pathList = ArrayList<String>()
             pathList.add(appDocPath)


             FilePickerBuilder.instance.setMaxCount(1)
                 .setSelectedFiles(pathList)
                 .setActivityTheme(R.style.LibAppTheme)
                 .pickFile(this, PICK_DOCUMENT_CODE)
 */
        })


        var clCamera = attachmentDialog!!.findViewById<ConstraintLayout>(R.id.cl_camera)




        clCamera.setOnClickListener(View.OnClickListener {

            attachmentDialog!!.dismiss()


            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                //Create a file to store the image
                var photoFile: File? = null;
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    outputFileUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        photoFile
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
            }


        })


        var clGallery = attachmentDialog!!.findViewById<ConstraintLayout>(R.id.cl_gallery)
        clGallery.setOnClickListener(View.OnClickListener {

            attachmentDialog!!.dismiss()
            try {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, GALLERY_REQUEST)
            } catch (e: Exception) {

                Utils.showToast(this, "No Gallery Found..")

            }

        })

    }

    var mimeTypes =
        arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "text/plain",
            "application/pdf"
        )


    var attachmentDialog: Dialog? = null
    var provider: ProviderModel? = null
    var patientDetail = ""
    var chatUser: ChatUsersModel? = null
    val db = FirebaseFirestore.getInstance()
    var preference: AppPreference? = null
    var user: UserModel? = null
    var chatMessages = ArrayList<ChatListModel>()
    var imagesChatMessages = ArrayList<ImageChatModel>()
    var chatListAdapter: ChatListAdapter? = null
    var imageChatMessageAdapter: ImageChatMessageAdapter? = null
    var imageCount = -1
    var outputFileUri: Uri? = null
    val CAMERA_REQUEST = 101
    val GALLERY_REQUEST = 102
    val PICK_DOCUMENT_CODE = 103
    var imageFilePath: String = ""
    var updatePosition = -1
    var sdCard = Environment.getExternalStorageDirectory()
    var appDocPath = sdCard.absolutePath + "/DoctorDesh"

    lateinit var mDatabase: DatabaseReference
    lateinit var mMyDatabaseUser: DatabaseReference
    lateinit var mUserDatabaseUser: DatabaseReference
    var valueEventListener: ValueEventListener? = null
    var childEventListener: ChildEventListener? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    var chatViewModel: ChatViewModel? = null


    /// MESSAGE TYPE VALUES ///
    /// 1. NORMAL MESSAGE
    /// 2. IMAGE FILES
    /// 3. TEXT FILES
    /// 4. PDF FILES


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initView()


    }

    fun initView() {

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        preference = AppPreference.getInstance(this)
        user = preference!!.getUserData()

        toolbar.setNavigationOnClickListener { finish() }

        rv_messages.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rv_messages.layoutManager = layoutManager
        chatListAdapter = ChatListAdapter(this, chatMessages)
        rv_messages.adapter = chatListAdapter
        iv_add_image.setOnClickListener(this)

        var imagesLayoutManager = LinearLayoutManager(this)
        imagesLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        imagesChatMessages = ArrayList()
        imageChatMessageAdapter = ImageChatMessageAdapter(this, imagesChatMessages, this)
        rv_images_list.setHasFixedSize(true)
        rv_images_list.layoutManager = imagesLayoutManager
        rv_images_list.adapter = imageChatMessageAdapter


        val sectionItemDecoration = RecyclerSectionItemDecoration(0, true, this)

        rv_messages.addItemDecoration(sectionItemDecoration)



        if (intent != null && intent.hasExtra("provider")) {

            patientDetail = intent.getStringExtra("patient_detail")


            val type = object : TypeToken<ProviderModel>() {}.type

            provider = Gson().fromJson<ProviderModel>(intent.getStringExtra("provider"), type)


            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(this)
                .load(provider!!.profilePic)
                .apply(options)
                .into(iv_profile_pic)


            tv_user_name!!.text = provider!!.firstName + " " + provider!!.lastName


/*
            var messages = db.collection("messages").document(user!!._id + "-" + provider!!._id).collection(
                "message"
            ).get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(task: Task<QuerySnapshot>) {
                        if (task.isSuccessful() && task.result!!.size() > 0) {

                            for (i in 0..task.result!!.size() - 1) {

                                var doc = task!!.result!!.elementAt(i)

                                val fields = StringBuilder("")
                                fields.append("id : ").append(doc!!.get("chat_id"))
                                fields.append("\nfrom : ").append(doc!!.get("from"))
                                fields.append("\ntime : ").append(doc!!.get("time"))
                                fields.append("\nName: ").append(doc!!.get("name"))
                                fields.append("\nEmail: ").append(doc.get("my_pic"))
                                fields.append("\nchat_message: ").append(doc.get("chat_message"))
                                Utils.showLog(fields.toString())


                                var chat = ChatListModel(
                                    doc!!.get("from").toString(),
                                    doc!!.get("name").toString(),
                                    doc.get("chat_message").toString(),
                                    doc!!.get("time").toString(),
                                    doc!!.get("chat_id").toString(),
                                    doc.get("my_pic").toString()
                                )
                                chatMessages.add(chat)

                            }

                            chatListAdapter!!.notifyDataSetChanged()
                            rv_messages.smoothScrollToPosition(chatMessages.size - 1)
                        }
                    }


                }) as DocumentReference
*/


            /* messages.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                  override fun onEvent(documentSnapshot: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                      if (p1 != null) {
                          Log.d("ERROR", p1.message);
                          return;
                      }
                      if (documentSnapshot != null && documentSnapshot.exists()) {


                          var chat = ChatListModel(
                              documentSnapshot!!.get("from").toString(),
                              documentSnapshot!!.get("name").toString(),
                              documentSnapshot.get("chat_message").toString(),
                              documentSnapshot!!.get("time").toString(),
                              documentSnapshot!!.get("chat_id").toString(),
                              documentSnapshot.get("my_pic").toString()
                          )
                          chatMessages.add(chat)
                          chatListAdapter!!.notifyItemInserted(chatMessages.size - 1)
                          rv_messages.smoothScrollToPosition(chatMessages.size - 1)
                      }
                  }
              })
  */
            mDatabase = FirebaseDatabase.getInstance().getReference("messages")
                .child(user!!._id + "-" + provider!!._id)
            mMyDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(user!!._id)
            mUserDatabaseUser =
                FirebaseDatabase.getInstance().getReference("users").child(provider!!._id)


            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {


                    if (!patientDetail.equals("")) {


                        var pic = ""
                        if (user?.profilePic != null) {
                            pic = user!!.profilePic
                        }


                        var chat = ChatListModel(
                            user!!._id,
                            patientDetail,
                            Date().time.toString(),
                            Date().time.toString(),
                            "1",
                            user!!.firstName + " " + user!!.lastName,
                            pic,
                            ""
                        )

                        patientDetail = ""

                        sendMessageToServer(chat, false)


                    }


                }


            })





            registerChildListener()


        } else if (intent != null && intent.hasExtra("chatUser")) {
            val type = object : TypeToken<ChatUsersModel>() {}.type

            chatUser = Gson().fromJson<ChatUsersModel>(intent.getStringExtra("chatUser"), type)

            var options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

            Glide.with(this)
                .load(chatUser!!.pic)
                .apply(options)
                .into(iv_profile_pic)

            tv_user_name!!.text = chatUser!!.name



            mDatabase =
                FirebaseDatabase.getInstance().getReference("messages").child(chatUser!!.chatId)
            mMyDatabaseUser =
                FirebaseDatabase.getInstance().getReference("users").child(chatUser!!.userId)
            mUserDatabaseUser =
                FirebaseDatabase.getInstance().getReference("users").child(chatUser!!.providerId)


            /*  valueEventListener = mDatabase.addValueEventListener(object : ValueEventListener {
                  override fun onCancelled(p0: DatabaseError) {

                  }

                  override fun onDataChange(snapshot: DataSnapshot) {


                      Log.e("Count ", "" + snapshot.getChildrenCount())


                  }


              })*/
            registerChildListener()

            updateMessageReadStatus()


        }
        iv_send.setOnClickListener(View.OnClickListener {

            if (!et_message.text.toString().equals("")) {


                var chat = ChatListModel(
                    user!!._id,
                    et_message.text.toString(),
                    Date().time.toString(),
                    Date().time.toString(),
                    "1",
                    user!!.firstName + " " + user!!.lastName,
                    user!!.profilePic,
                    ""
                )

                sendMessageToServer(chat, false)


            }
            imageCount = imagesChatMessages.size
            if (imageCount > 0)
                uploadImageFiles()

        })


    }


    fun uploadImageFiles() {
        imageCount = imageCount - 1
        if (imageCount >= 0) {


            uploadImage(imagesChatMessages.get(imageCount), imageCount)


        } else {
            imageCount = -1

            if (imagesChatMessages.size == 0)
                rv_images_list.visibility = View.GONE
        }


    }


    fun sendMessageToServer(chat: ChatListModel, fromDocument: Boolean) {


        if (provider != null) {
            chat.recieverName = provider!!.firstName + " " + provider!!.lastName

            chat.senderName = user!!.firstName + " " + user!!.lastName


        } else {


            if (chatUser!!.userId.equals(user!!._id)) {
                chat.senderName = user!!.firstName + " " + user!!.lastName

                chat.recieverName = chatUser!!.name


            } else {
                chat.recieverName = chatUser!!.name

                chat.senderName = user!!.firstName + " " + user!!.lastName
            }
        }


        var key = mDatabase.push().key
        mDatabase.child(key!!).setValue(chat)








        if (fromDocument && updatePosition != -1) {

            chatMessages.get(updatePosition).isUploading = false
            chatListAdapter!!.notifyItemChanged(updatePosition)
            updatePosition = -1


        }




        if (provider != null) {


            var deviceToken = ""
            if (provider!!.deviceToken != null)
                deviceToken = provider!!.deviceToken


            var chatUser1 = ChatUsersModel(
                user!!._id + "-" + provider!!._id,
                chat.message,
                chat.messageTime,
                "0",
                chat.messageType,
                chat.imageUrl,
                provider!!.firstName,
                provider!!.profilePic,
                false,
                false, user!!._id, provider!!._id, deviceToken
            )
            mMyDatabaseUser.child(provider!!._id).setValue(chatUser1)


            var pic = ""
            if (user!!.profilePic != null) {
                pic = user!!.profilePic
            }


            var chatUser2 = ChatUsersModel(
                user!!._id + "-" + provider!!._id,
                chat.message,
                chat.messageTime,
                "0",
                chat.messageType,
                chat.imageUrl,
                user!!.firstName,
                pic,
                false,
                false,
                provider!!._id,
                user!!._id,
                user!!.deviceToken
            )







            mUserDatabaseUser.child(user!!._id).setValue(chatUser2)


            var providerDeviceToken: String = ""
            if (provider!!.deviceToken != null)
                providerDeviceToken = provider!!.deviceToken

            sendNotificationToUser(
                user!!.firstName + " " + user!!.lastName,
                et_message.text.toString(),
                providerDeviceToken
            )


        } else {


            var chatUser1 = ChatUsersModel(
                chatUser!!.chatId,
                chat.message,
                chat.messageTime,
                "0",
                chat.messageType,
                chat.imageUrl,
                chatUser!!.name,
                chatUser!!.pic, false, false,
                chatUser!!.userId,
                chatUser!!.providerId,
                chatUser!!.deviceToken

            )




            mMyDatabaseUser.child(chatUser!!.providerId).setValue(chatUser1)


            var chatUser2 = ChatUsersModel(
                chatUser!!.chatId,
                chat.message,
                chat.messageTime,
                "0",
                chat.messageType,
                chat.messageType,
                user!!.firstName,
                user!!.profilePic,
                false,
                false,
                chatUser!!.providerId,
                chatUser!!.userId,
                user!!.deviceToken
            )




            mUserDatabaseUser.child(chatUser!!.userId).setValue(chatUser2)

            chatUser!!.deviceToken?.let {
                sendNotificationToUser(
                    user!!.firstName + " " + user!!.lastName,
                    et_message.text.toString(), it
                )
            }?.run {
                sendNotificationToUser(
                    user!!.firstName + " " + user!!.lastName,
                    et_message.text.toString(), ""
                )
            }
        }




        Handler().postDelayed(Runnable {

            rv_messages.smoothScrollToPosition(rv_messages.adapter!!.itemCount - 1)

        }, 200)






        et_message.setText("")


    }

    /*fun addMyItemToDB(chat: ChatListModel) {
        /////////////// ADDING CHAT INIT INTO USER TABLE ////////////////
        if (provider != null) {
            val mUser = HashMap<String, String>()
            mUser.put("chat_message", "" + chat.message)
            mUser.put("chat_time", "" + chat.messageTime)
            mUser.put("user_name", "" + provider!!.firstName)
            mUser.put("profile_pic", "" + provider!!.profilePic)
            mUser.put("chat_id", "" + user!!._id + "-" + provider!!._id)
            mUser.put("is_message_read", "0")
            mUser.put("user_id", "" + user!!._id)
            mUser.put("provider_id", "" + provider!!._id)


            db.collection("users").document(user!!._id).collection("chat_users")
                .document(provider!!._id)
                .set(mUser)
                .addOnSuccessListener {
                    Utils.showToast(this, "User added successfully!")
                }
                .addOnFailureListener {

                }


            val mProvider = HashMap<String, String>()
            mProvider.put("chat_message", "" + chat.userMessage)
            mProvider.put("chat_time", "" + chat.messageTime)
            mProvider.put("user_name", "" + user!!.firstName)
            mProvider.put("profile_pic", "" + user!!.profilePic)
            mProvider.put("chat_id", "" + user!!._id + "-" + provider!!._id)
            mProvider.put("is_message_read", "0")
            mProvider.put("user_id", "" + user!!._id)
            mProvider.put("provider_id", "" + provider!!._id)

            db.collection("users").document(provider!!._id).collection("chat_users")
                .document(user!!._id)
                .set(mProvider)
                .addOnSuccessListener {
                    Utils.showToast(this, "User added successfully!")
                }
                .addOnFailureListener {

                }

        } else {


            val mUser = HashMap<String, String>()
            mUser.put("chat_message", "" + chat.userMessage)
            mUser.put("chat_time", "" + chat.messageTime)
            mUser.put("user_name", "" + chatUser!!.user_name)
            mUser.put("profile_pic", "" + chatUser!!.profile_pic)
            mUser.put("chat_id", chatUser!!.chat_id)
            mUser.put("is_message_read", "0")
            mUser.put("user_id", "" + chatUser!!.userId)
            mUser.put("provider_id", "" + chatUser!!.providerId)

            db.collection("users").document(chatUser!!.userId).collection("chat_users")
                .document(chatUser!!.providerId)
                .set(mUser)
                .addOnSuccessListener {
                }
                .addOnFailureListener {

                }


            val mProvider = HashMap<String, String>()
            mProvider.put("chat_message", "" + chat.userMessage)
            mProvider.put("chat_time", "" + chat.messageTime)
            mProvider.put("user_name", "" + chatUser!!.user_name)
            mProvider.put("profile_pic", "" + chatUser!!.profile_pic)
            mProvider.put("chat_id", chatUser!!.chat_id)
            mProvider.put("is_message_read", "0")
            mProvider.put("user_id", "" + chatUser!!.userId)
            mProvider.put("provider_id", "" + chatUser!!.providerId)

            db.collection("users").document(chatUser!!.providerId).collection("chat_users")
                .document(chatUser!!.userId)
                .set(mProvider)
                .addOnSuccessListener {
                }
                .addOnFailureListener {

                }
        }

//////////////////////////////////////////////////////////////////////////////////////
    }
*/

    fun registerChildListener() {


        childEventListener = mDatabase.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {


            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                Log.e("CallMe4", "onChildAdded called")
                try {
                    var data = dataSnapshot.getValue(ChatListModel::class.java)

                    Utils.showLog("Message : " + data.toString())

                    chatMessages?.add(data!!)
                    chatListAdapter!!.notifyDataSetChanged()
                    rv_messages.smoothScrollToPosition(chatMessages.size - 1)
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
        if (childEventListener != null)
            mDatabase.removeEventListener(childEventListener!!)
        if (valueEventListener != null)
            mDatabase.removeEventListener(valueEventListener!!)
    }

    fun requestPermission() {

        var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        ActivityCompat.requestPermissions(this, permissions, 123);


    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
                return true
            } else {
                return false
            }

        } else {
            return true
        }


    }

    fun openImagePicker() {


        var pickerDialog = Dialog(this)
        pickerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pickerDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pickerDialog!!.setContentView(R.layout.dialog_image_picker)
        pickerDialog!!.show()
        pickerDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        pickerDialog.show()

        var tvCamera = pickerDialog.findViewById<TextView>(R.id.tv_camera)




        tvCamera.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()


            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                //Create a file to store the image
                var photoFile: File? = null;
                try {
                    photoFile = createImageFile();
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    outputFileUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        photoFile
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
            }


        })


        var tvGallery = pickerDialog.findViewById<TextView>(R.id.tv_gallery)
        tvGallery.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()
            try {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, GALLERY_REQUEST)
            } catch (e: Exception) {

                Utils.showToast(this, "No Gallery Found..")

            }

        })

    }


    private fun createImageFile(): File {
        var timeStamp =
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date());
        var imageFileName = "IMG_" + timeStamp + "_";
        var storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) run {

            try {
                //val selectedFilePath = RealPathUtil.getPath(this@ProfileActivity, uri)

                val compressedImage = compressImage(imageFilePath)

                if (rv_images_list.visibility == View.GONE)
                    rv_images_list.visibility = View.VISIBLE

                imagesChatMessages.add(ImageChatModel(compressedImage, false))
                imageChatMessageAdapter!!.notifyItemInserted(imagesChatMessages.size - 1)
                // imageChatMessageAdapter!!.notifyDataSetChanged()


                /*   Glide.with(this)
                       .load(compressedImage)
                       .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                       .into(profile_image)

                   uploadImage(compressedImage)
   */

            } catch (e: IOException) {
                e.printStackTrace()
            }


        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) run {
            val uri = data!!.getData()
            try {
                val selectedFilePath = RealPathUtil.getPath(this@ChatActivity, uri)

                val compressedImage = compressImage(selectedFilePath)

                imagesChatMessages.add(ImageChatModel(compressedImage, false))
                imageChatMessageAdapter!!.notifyItemInserted(imagesChatMessages.size - 1)
                //imageChatMessageAdapter!!.notifyDataSetChanged()


                if (rv_images_list.visibility == View.GONE)
                    rv_images_list.visibility = View.VISIBLE

                /* Glide.with(this)
                     .load(compressedImage)
                     .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                     .into(profile_image)

                 uploadImage(compressedImage)
 */
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else if (requestCode == PICK_DOCUMENT_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                var uri = data.data

                if (uri != null) {

                    val mimeType: String? = data.data?.let { returnUri ->
                        contentResolver.getType(returnUri)
                    }

                    var filePath = uri.toString()


//                    var extension = filePath.substring(filePath.lastIndexOf("."))

                    //                  Utils.showLog("File name : " + filePath + " extension : " + extension)

                    //                 var filename = filePath.substring(filePath.lastIndexOf("/") + 1)


                    var filename = "file_" + Date().time.toString() + getFileExtension(mimeType!!)
                    // var filename = "file_"


                    if (data.clipData != null) {
                        val clipData = data.clipData


                        for (i in 0 until clipData!!.itemCount) {
                            val path = clipData.getItemAt(i)
                            Log.e("PDF2EXL", "Path:" + path.uri.toString())
                            saveFileFromDrive(path.uri, filename)


                        }
                    } else {
                        val path = data.data
                        Log.e("PDF2EXL", "Path:" + path!!.toString())
                        saveFileFromDrive(path, filename)
                    }


                    /*



                    var filePath = uri.toString()
                    var extension = filePath.substring(filePath.lastIndexOf("."))

                    Utils.showLog("File name : " + filePath + " extension : " + extension)

                    var filename = filePath.substring(filePath.lastIndexOf("/") + 1)*/


                    var chat = ChatListModel(
                        user!!._id,
                        filename
                        ,
                        Date().time.toString(),
                        Date().time.toString(),
                        "3",
                        user!!.firstName + " " + user!!.lastName,
                        user!!.profilePic,
                        "",
                        true

                    )

                    chatMessages.add(chat)
                    chatListAdapter!!.notifyItemChanged(chatMessages.size - 1)


                    uploadFile(
                        appDocPath + "/" + filename,
                        getFileExtension(mimeType),
                        chatMessages.size - 1
                    )


                }


            }
        }
    }


    fun getFileExtension(mimeType: String): String {


        /*arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "text/plain",
            "application/pdf"
        )*/


        var itemPos = mimeTypes.indexOf(mimeType)
        when (itemPos) {
            0 -> {
                return ".doc"
            }
            1 -> {
                return ".docx"
            }
            2 -> {
                return ".txt"
            }
            3 -> {
                return ".pdf"
            }
        }

        return ""
    }


    fun saveFileFromDrive(uri: Uri, name: String) {

        val directory = File(appDocPath)
        if (!directory.isDirectory()) {
            directory.mkdirs()
        }
        val tempFile = File(directory, name)


        var bis: BufferedInputStream? = null;
        var bos: BufferedOutputStream? = null;

        try {
            bis = BufferedInputStream(getContentResolver().openInputStream(uri))
            bos = BufferedOutputStream(FileOutputStream(tempFile))
            val buf = ByteArray(8192)
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (e: IOException) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }
    }


    fun getRealPathFromURI(uri: Uri): String {
        var projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        var cursor = managedQuery(uri, projection, null, null, null);
        var column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (grantResults.size > 0) {

            var CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            var readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            var writeExternalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (CameraPermission && readExternalStorage && writeExternalStorage) {
                openAttachmentDialog()
            } else {
                Utils.showToast(this, getString(R.string.msg_incomplete_permission))
            }

        }


    }

    fun compressImage(imageUri: String): String {

        var filePath = getRealPathFromURI(imageUri)
        var scaledBitmap: Bitmap? = null

        var options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        val maxHeight = 816.0f
        val maxWidth = 612.0f

        var imgRatio = (actualWidth / actualHeight).toFloat()


        var maxRatio = maxWidth / maxHeight

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        //      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap!!.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)

            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename

    }

    fun getRealPathFromURI(contentURI: String): String {
        var contentUri = Uri.parse(contentURI);
        var filePathColumn = arrayOf(MediaStore.Images.Media.DATA)


        var cursor = getContentResolver().query(contentUri, filePathColumn, null, null, null);
        if (cursor == null) {
            return contentUri.getPath()!!
        } else {
            cursor.moveToFirst();
            var index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        var height = options.outHeight;
        var width = options.outWidth;
        var inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            var heightRatio = Math.round((height.toFloat() / reqHeight.toFloat()))
            var widthRatio = Math.round((width.toFloat() / reqWidth.toFloat()))

            if (heightRatio < widthRatio)
                inSampleSize = heightRatio
            else
                inSampleSize = widthRatio


        }
        var totalPixels = width * height;
        var totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    fun getFilename(): String {
        var file = File(
            Environment.getExternalStorageDirectory().getPath(),
            resources.getString(R.string.app_name) + "/Images"
        );
        if (!file.exists()) {
            file.mkdirs();
        }
        var uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    fun uploadImage(image: ImageChatModel, pos: Int) {
        imagesChatMessages.get(pos).isUploading = true
        imageChatMessageAdapter!!.notifyItemChanged(pos)

        var file = Uri.fromFile(File(image.imagePath))
        val childRef = storageReference.child("" + Date().time.toString() + ".jpg")
        childRef.putFile(file)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    //val downloadUri = taskSnapshot!!.getStorage().getDownloadUrl()


                    childRef.getDownloadUrl()
                        .addOnCompleteListener(object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {


                                var profileImageUrl = task.getResult().toString()
                                Utils.showLog(profileImageUrl)

                                var chat = ChatListModel(
                                    user!!._id,
                                    "",
                                    Date().time.toString(),
                                    Date().time.toString(),
                                    "2",
                                    user!!.firstName + " " + user!!.lastName,
                                    user!!.profilePic,
                                    profileImageUrl
                                )

                                sendMessageToServer(chat, false)



                                imagesChatMessages.removeAt(imageCount)
                                imageChatMessageAdapter!!.notifyItemRemoved(imageCount)


                                uploadImageFiles()


                            }
                        })


                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {

                    Utils.showLog(getString(R.string.msg_common_error))
                    imagesChatMessages.get(pos).isUploading = false
                    imageChatMessageAdapter!!.notifyItemChanged(pos)
                }
            })
            .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                override fun onProgress(p0: UploadTask.TaskSnapshot?) {

                }
            })


    }


    fun uploadFile(filePath: String, fileType: String, position: Int) {

        updatePosition = position
        var file = Uri.fromFile(File(filePath))
        val childRef = storageReference.child("" + Date().time.toString() + fileType)
        childRef.putFile(file)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    //val downloadUri = taskSnapshot!!.getStorage().getDownloadUrl()


                    childRef.getDownloadUrl()
                        .addOnCompleteListener(object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {


                                var profileImageUrl = task.getResult().toString()
                                Utils.showLog(profileImageUrl)

                                var chat = chatMessages.get(position)

                                chat.imageUrl = profileImageUrl
                                chat.isUploading = false

                                sendMessageToServer(chat, true)


                                chatListAdapter!!.notifyItemChanged(position)


                            }
                        })


                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {

                    Utils.showLog(getString(R.string.msg_common_error))
                    chatMessages.get(position).isUploading = false
                    chatListAdapter!!.notifyItemChanged(position)
                }
            })
            .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                override fun onProgress(p0: UploadTask.TaskSnapshot?) {

                }
            })


    }


    fun updateMessageReadStatus() {

        var chatUser1 = ChatUsersModel(
            chatUser!!.chatId,
            chatUser!!.chatMessage,
            chatUser!!.chatTime,
            "1",
            chatUser!!.messageType,
            chatUser!!.imageUrl,
            chatUser!!.name,
            chatUser!!.pic, false, false,
            chatUser!!.userId,
            chatUser!!.providerId,
            chatUser!!.deviceToken
        )




        mMyDatabaseUser.child(chatUser!!.providerId).setValue(chatUser1)


        var chatUser2 = ChatUsersModel(
            chatUser!!.chatId,
            chatUser!!.chatMessage,
            chatUser!!.chatTime,
            "1",
            chatUser!!.messageType,
            chatUser!!.messageType,
            user!!.firstName,
            user!!.profilePic, false, false,
            chatUser!!.userId,
            chatUser!!.providerId,
            chatUser!!.deviceToken
        )




        mUserDatabaseUser.child(chatUser!!.userId).setValue(chatUser2)


    }


    fun sendNotificationToUser(userName: String, userMessage: String, deviceToken: String) {

        var id = ""
        if (provider != null) {
            id = provider!!._id


        } else {


            if (chatUser!!.userId.equals(user!!._id))
                id = chatUser!!.providerId
            else
                id = chatUser!!.userId
        }

        chatViewModel!!.sendNotification(this, id, userName, userMessage, deviceToken)
            .observe(this, androidx.lifecycle.Observer {


                if (it != null)
                    Utils.showLog(it.toString())


            })
    }

    private fun recursiveDelete(file: File) {
        //to end the recursive loop
        if (!file.exists())
            return

        //if directory, go inside and call recursively
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                //call recursively
                recursiveDelete(f)
            }
        }
        //call delete to delete files and empty directory
        file.delete()
    }


}
