package com.doctordesh.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import com.doctordesh.helpers.AppPreference
import com.doctordesh.helpers.Utils
import com.doctordesh.models.UserModel
import com.doctordesh.viewModels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import com.myhexaville.smartimagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URI
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.doctordesh.helpers.RealPathUtil
import com.google.gson.Gson
import com.google.gson.JsonObject

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Handler
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.doctordesh.adapters.CredentialsAdapter
import com.doctordesh.models.CredentialModel
import kotlinx.android.synthetic.main.activity_profile.toolbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.collections.ArrayList


class ProfileActivity : AppCompatActivity(), View.OnClickListener {


    var isEditable = false


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.tv_settings -> {
                startActivity(Intent(this@ProfileActivity, SettingsActivity::class.java))
            }
            R.id.tv_about -> {

                startActivity(
                    Intent(
                        this@ProfileActivity,
                        WebViewActivity::class.java
                    ).putExtra("from", "about_us")
                )

            }
            R.id.btn_logout -> {
                logout()
            }
            R.id.iv_image_picker -> {


                if (checkPermission())
                    openImagePicker()
                else {
                    requestPermission()
                }


            }
            R.id.iv_done -> {
                /*  if(isEditable)
                  {
                      profileViewModel!!.updateProfile(this, et_first_name.text.toString(), et_last_name.text.toString(),"", et_email.text.toString(),et_current_password.text.toString(),et_new_password.text.toString()).observe(this, Observer {


                      })
                  }
                  else
                  {
                      isEditable=true
                  }

  */


                showEditProfileDialog()


            }

        }
    }


    fun requestPermission() {

        var permissions = arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)


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


    fun uploadImage(imageUri: String) {

        var file = File(imageUri)
        var requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        var img = MultipartBody.Part.createFormData("profilePic", file.name, requestFile)


        profileViewModel!!.updateProfilePic(this, img).observe(this, Observer {

            if (it != null && it.has("status") && it.get("status").asString.equals("1")) {

                Utils.showToast(this, it.get("message").asString)

                if (it.has("user_info") && it.get("user_info") is JsonObject) {

                    /*  var userInfo = it.getAsJsonObject("user_info")

                      if (userInfo.has("profilePic")) {
                          user!!.profilePic = userInfo.get("profilePic").asString

                          AppPreference.getInstance(this@ProfileActivity).setUserData(Gson().toJson(user))

                      }*/


                    AppPreference.getInstance(this@ProfileActivity)
                        .setUserData(it.get("user_info").toString())


                }

            }


            pb_image.visibility = View.GONE
        })


    }

    fun logout() {
        AppPreference.getInstance(this).setUserData("")
        var intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    var imagePicker: ImagePicker? = null
    var user: UserModel? = null
    var profileViewModel: ProfileViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initView()

        Utils.hideSoftKeyboard(this)

    }

    var preference: AppPreference? = null
    var outputFileUri: Uri? = null
    val CAMERA_REQUEST = 101
    val GALLERY_REQUEST = 102
    var imageFilePath: String = ""
    fun initView() {
        preference = AppPreference.getInstance(this)
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = AppPreference.getInstance(this@ProfileActivity).getUserData()

        if (user != null) {
            et_name.setText(user!!.firstName + " " + user!!.lastName)
            et_credentials.setText(user!!.credentials)
            et_email.setText(user!!.email)
            et_phone_number.setText(user!!.phoneNumber)
            Glide.with(this)
                .load(user!!.profilePic)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(profile_image)

        } else {
            logout()
        }

        et_credentials.keyListener = null
        et_email.keyListener = null
        et_name.keyListener = null
        et_phone_number.keyListener= null

        et_credentials.isFocusable = false
        et_email.isFocusable = false
        et_name.isFocusable = false
        et_phone_number.isFocusable = false
        tv_version.text = Utils.getAppVersion(this)
        toolbar.setNavigationOnClickListener { finish() }
        iv_image_picker.setOnClickListener(this)
        tv_settings.setOnClickListener(this)
        tv_about.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
        iv_done.setOnClickListener(this)


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

        var clCamera = pickerDialog.findViewById<ConstraintLayout>(R.id.cl_camera)




        clCamera.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()

/*
            var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

            dir = dir + "/Images"
            var fileDir = File(dir);

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }


            var file = dir + Date().time + ".jpg";
            var newfile = File(file);
            try {
                newfile.createNewFile();
            } catch (e: IOException) {
            }

            outputFileUri = Uri.fromFile(newfile);
*/

            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                //Create a file to store the image
                var photoFile: File? = null;
                try {
                    photoFile = createImageFile();
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    outputFileUri =
                        FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
            }


        })


        var clGallery = pickerDialog.findViewById<ConstraintLayout>(R.id.cl_gallery)
        clGallery.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()
            try {
                val galleryIntent =
                    Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, GALLERY_REQUEST)
            } catch (e: Exception) {

                Utils.showToast(this, "No Gallery Found..")

            }

        })

    }


    private fun createImageFile(): File {
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date());
        var imageFileName = "IMG_" + timeStamp + "_";
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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



                Glide.with(this)
                    .load(compressedImage)
                    .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                    .into(profile_image)

                uploadImage(compressedImage)

                /* mBitmap = SiliCompressor.with(getActivity()).getCompressBitmap(selectedFilePath)
                if (mBitmap != null) {
                    startCropActivity(mBitmap)
                }*/
            } catch (e: IOException) {
                e.printStackTrace()
            }


        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) run {
            val uri = data!!.getData()
            try {
                val selectedFilePath = RealPathUtil.getPath(this@ProfileActivity, uri)

                val compressedImage = compressImage(selectedFilePath)

                Glide.with(this)
                    .load(compressedImage)
                    .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                    .into(profile_image)

                uploadImage(compressedImage)

                /* mBitmap = SiliCompressor.with(getActivity()).getCompressBitmap(selectedFilePath)
                if (mBitmap != null) {
                    startCropActivity(mBitmap)
                }*/
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (grantResults.isNotEmpty()) {

            var CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            var readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            var writeExternalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (CameraPermission && readExternalStorage && writeExternalStorage) {
                openImagePicker()
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
            Paint(FILTER_BITMAP_FLAG)
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


    fun showEditProfileDialog() {


        var selectedCredential: CredentialModel? = null

        var credentialsList = ArrayList<CredentialModel>()


        fun getUserCredential(): Int {
            var defaultCredential = 0
            if (user != null && credentialsList.size != 0) {
                for (i in 0..credentialsList.size) {
                    if (credentialsList.get(i).credential_type.equals(user!!.credentials)) {
                        defaultCredential = i

                        break

                    }

                }
            }

            return defaultCredential


        }


        var editProfileDialog = Dialog(this)
        editProfileDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        editProfileDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editProfileDialog!!.setContentView(R.layout.dialog_edit_profile)
        editProfileDialog!!.show()
        editProfileDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        editProfileDialog.show()


        var etFirstName = editProfileDialog.findViewById<EditText>(R.id.et_first_name)
        var etLastName = editProfileDialog.findViewById<EditText>(R.id.et_last_name)
        var llPassword = editProfileDialog.findViewById<LinearLayout>(R.id.ll_password)
        var etOldPassword = editProfileDialog.findViewById<EditText>(R.id.et_current_password)
        var etNewPassword = editProfileDialog.findViewById<EditText>(R.id.et_new_password)
        var etPhoneNumber = editProfileDialog.findViewById<EditText>(R.id.et_phone_number)



        if (user != null) {
            etFirstName.setText(user!!.firstName)
            etLastName.setText(user!!.lastName)
            etPhoneNumber.setText(user!!.phoneNumber)
        }


        var spinnerCredentials = editProfileDialog.findViewById<Spinner>(R.id.spinner_credentials)

        if (preference != null) {
            credentialsList = preference!!.getCredentials()
            var credentialsAdapter = CredentialsAdapter(this, credentialsList)
            spinnerCredentials.adapter = credentialsAdapter



            selectedCredential = credentialsList.get(getUserCredential())


        }

        spinnerCredentials.setSelection(getUserCredential())

        spinnerCredentials.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedCredential = credentialsList.get(position)
            }
        }

        var swPassword = editProfileDialog.findViewById<Switch>(R.id.sw_password)
        swPassword.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            run {

                if (isChecked) {
                    llPassword.visibility = View.VISIBLE
                } else {
                    llPassword.visibility = View.GONE
                }
            }
        })


        var btnSave = editProfileDialog.findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener(View.OnClickListener {

            if (!Utils.isInternetAvailable(this)) {
                Utils.showToast(this, resources.getString(R.string.msg_no_internet))

            } else if (etFirstName.text!!.length < 2) {
                Utils.showToast(this, resources.getString(R.string.msg_invalid_name))

            } else if (etLastName.text!!.length < 2) {
                Utils.showToast(this, resources.getString(R.string.msg_invalid_name))

            }
            else if (etPhoneNumber.text!!.length < 6) {
                Utils.showToast(this, resources.getString(R.string.msg_invalid_phone_number))

            }
            else if (selectedCredential!!._id.equals("00")) {
                Utils.showToast(this, resources.getString(R.string.msg_invalid_credential))

            } else if (swPassword.isChecked && etOldPassword.text!!.length == 0) {
                Utils.showToast(this, resources.getString(R.string.msg_empty_current_pass))

            } else if (swPassword.isChecked && etNewPassword.text!!.length == 0) {
                Utils.showToast(this, resources.getString(R.string.msg_empty_pass))

            } else if (swPassword.isChecked && etNewPassword.text!!.length < 6) {
                Utils.showToast(this, resources.getString(R.string.msg_invalid_pass))


            } else {
                profileViewModel!!.updateProfile(
                    this,
                    etFirstName.text.toString(),
                    etLastName.text.toString(),
                    "+1"+etPhoneNumber.text.toString(),
                    selectedCredential!!.credential_type,
                    user!!.email,
                    etOldPassword.text.toString(),
                    etNewPassword.text.toString()
                ).observe(

                    this, Observer {

                        if (it.has("status") && it.get("status").asString.equals("1")) {
                            Utils.showLog(it.toString())


                            if (it.has("user_info")) {
                                AppPreference.getInstance(this)
                                    .setUserData(it.get("user_info").toString())

                                Utils.showToast(this, it.get("message").toString())

                                Handler().postDelayed(Runnable { editProfileDialog.dismiss() }, 900)



                                refreshScreen()


                            }


                        } else {
                            if (it.has("message"))
                                Utils.showToast(this, it.get("message").asString)
                        }

                    }


                )
            }

        })


    }

    fun refreshScreen() {


        user = AppPreference.getInstance(this@ProfileActivity).getUserData()

        if (user != null) {
            et_name.setText(user!!.firstName + " " + user!!.lastName)
            et_credentials.setText(user!!.credentials)
            et_email.setText(user!!.email)
            et_phone_number.setText(user!!.phoneNumber)
            Glide.with(this)
                .load(user!!.profilePic)
                .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(profile_image)

        }


    }


}
