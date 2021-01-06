package com.doctordesh.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.doctordesh.R
import com.doctordesh.adapters.SendPatientDocumentAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.viewModels.EmployeeReceiptsViewModel
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.android.synthetic.main.activity_employee_receipt.*
import kotlinx.android.synthetic.main.activity_employee_receipt.btn_submit
import kotlinx.android.synthetic.main.activity_employee_receipt.iv_scanned_image
import kotlinx.android.synthetic.main.activity_employee_receipt.rv_scanned_image
import kotlinx.android.synthetic.main.activity_employee_receipt.toolbar
import kotlinx.android.synthetic.main.activity_employee_receipt.tv_add_more_image
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EmployeeReceiptsActivity : AppCompatActivity(){
    private val REQUEST_CODE = 99
    private var fileName = ""
    private var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    var selectedDate = ""
    var file: File? = null
    var employeeReceiptViewModel: EmployeeReceiptsViewModel?= null
    var bitmapArrayList= ArrayList<Bitmap>()
    var fileArrayList= ArrayList<File>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_receipt)


        initViews()
    }

    private fun initViews() {
        employeeReceiptViewModel = ViewModelProviders.of(this).get(EmployeeReceiptsViewModel::class.java)

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        cl_add_receipts.setOnClickListener(View.OnClickListener {
            if (checkPermission()) {
                openImagePicker()
            } else {
                requestPermission()
            }
        })


        btn_submit.setOnClickListener(View.OnClickListener {
            openPatientDetailDialog()
        })

        tv_add_more_image.setOnClickListener {
            cl_add_receipts.visibility = View.VISIBLE
            iv_scanned_image.visibility = View.GONE
            cl_add_receipts.visibility = View.VISIBLE
            hideShareButton()
        }

    }


    fun openPatientDetailDialog() {
        var patientDetailDialog = Dialog(this)
        patientDetailDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        patientDetailDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        patientDetailDialog!!.setContentView(R.layout.dialog_patient_detail)
        patientDetailDialog!!.show()
        patientDetailDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        patientDetailDialog.show()


        var tvPatientDetail = patientDetailDialog.findViewById<TextView>(R.id.tv_patient_detail)
        tvPatientDetail.text = this.resources.getString(R.string.text_employee_detail)

        var etPatientName = patientDetailDialog.findViewById<EditText>(R.id.et_patient_name)
        etPatientName.hint = this.resources.getString(R.string.hint_employee_name)

        var etEmployeeType = patientDetailDialog.findViewById<EditText>(R.id.et_employee_type)
        etEmployeeType.hint = this.resources.getString(R.string.hint_employee_type)

        var etEmployeeAmount = patientDetailDialog.findViewById<EditText>(R.id.et_employee_amount)
        etEmployeeAmount.hint = this.resources.getString(R.string.hint_employee_amount)

        var tvPatientDOB = patientDetailDialog.findViewById<EditText>(R.id.tv_patient_dob)
        tvPatientDOB.hint = this.resources.getString(R.string.hint_patient_dob)
        tvPatientDOB.visibility = View.GONE

        var tvCancel = patientDetailDialog.findViewById<TextView>(R.id.tv_cancel)
        tvCancel.setOnClickListener(View.OnClickListener {
            patientDetailDialog.dismiss()
        })


        var tvSubmit = patientDetailDialog.findViewById<TextView>(R.id.tv_submit)

        tvSubmit.setOnClickListener(View.OnClickListener {

            if (etPatientName.text.toString().isEmpty()) {
                etPatientName.setError("Please enter employee name")
            } else if (etEmployeeType.text.toString().isEmpty()) {
                etEmployeeType.setError("Please enter employee type")
            } else if (etEmployeeAmount.text.toString().isEmpty()) {
                etEmployeeAmount.setError("Please enter employee amount")
            }
            else {
                submitEmployeeReceipts(etPatientName.text.toString(), etEmployeeType.text.toString(), etEmployeeAmount.text.toString())
                patientDetailDialog.dismiss()
            }

        })


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

    fun requestPermission() {
        var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, 123);
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

        clCamera.visibility = View.GONE

        clCamera.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()

            val intent = Intent(
                this,
                ScanActivity::class.java
            )
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)

            startActivityForResult(intent, REQUEST_CODE)
        })


        var clGallery = pickerDialog.findViewById<ConstraintLayout>(R.id.cl_gallery)
        clGallery.setOnClickListener(View.OnClickListener {

            pickerDialog.dismiss()
            try {

                val intent = Intent(
                    this,
                    ScanActivity::class.java
                )
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA)
                startActivityForResult(intent, REQUEST_CODE)
                //  cl_add_doc.visibility = View.GONE
            } catch (e: Exception) {

                Utils.showToast(this, "No Gallery Found..")
                hideShareButton()
                cl_add_receipts.visibility = View.VISIBLE
            }

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            cl_add_receipts.visibility = View.GONE
            val uri = data!!.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
            var bitmap: Bitmap? = null
            try {
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                    ImageDecoder.decodeBitmap(source)
                }

                contentResolver.delete(uri!!, null, null)
                iv_scanned_image.setImageBitmap(bitmap)
                // added by pallavi
                bitmapArrayList.add(bitmap!!)

                rv_scanned_image.setHasFixedSize(true)
                rv_scanned_image.layoutManager = GridLayoutManager(this,3)
                var adapter = SendPatientDocumentAdapter(this, bitmapArrayList)
                rv_scanned_image.adapter = adapter

                storeImage(bitmap!!)
            } catch (e: IOException) {
                hideShareButton()
                e.printStackTrace()
            }
        } else {
            cl_add_receipts.visibility = View.VISIBLE
        }
    }

    fun convertToPDF(imageFile: File) {
        try {

            var document = Document()
            fileName = Date().time.toString() + ".pdf"

            val mediaStorageDir = File(filesDir, "doctordesh" + File.separator + "files")
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {

                }
            }

            file=File(mediaStorageDir,fileName)
            fileArrayList.add(file!!)

            PdfWriter.getInstance(
                document, FileOutputStream(mediaStorageDir.path+File.separator+fileName)
            )

            document.open()

            val image = Image.getInstance(imageFile.absolutePath)  // Change image's name and extension.

            val scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.width) * 100; // 0 means you have no indentation. If you have any, change it.
            image.scalePercent(scaler);
            image.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP

            document.add(image)
            document.close()

            showShareButton()

        } catch (e: java.lang.Exception) {
            hideShareButton()
            Toast.makeText(this, "" + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun showShareButton() {
        rv_scanned_image.visibility = View.VISIBLE
        tv_add_more_image.visibility = View.VISIBLE
        btn_submit.visibility = View.VISIBLE
    }

    fun hideShareButton() {
        rv_scanned_image.visibility = View.GONE
        tv_add_more_image.visibility = View.GONE
        btn_submit.visibility = View.GONE
    }


    fun submitEmployeeReceipts(employeeName: String, type: String, amount: String) {

       // var requestFile = RequestBody.create(MediaType.parse("application/pdf"), file)
       // var img = MultipartBody.Part.createFormData("reciept", file!!.name, requestFile)



        var totalImageList = ArrayList<MultipartBody.Part>()
        for(fileItem in fileArrayList){
            var requestFile = RequestBody.create(MediaType.parse("application/pdf"), fileItem)
            var part = MultipartBody.Part.createFormData("reciept", file!!.name, requestFile)
            totalImageList.add(part)
        }


        employeeReceiptViewModel!!.sendEmployeeReceipts(this, employeeName, type, amount, totalImageList)
            .observe(this,
                androidx.lifecycle.Observer {

                    if (it != null && it.has("status") && it.get("status").asString.equals("1")) {
                        Utils.showLog(it.toString())
                        Utils.showToast(this, it["message"].toString())

                        bitmapArrayList.clear()
                        fileArrayList.clear()
                        cl_add_receipts.visibility = View.VISIBLE
                        iv_scanned_image.visibility = View.GONE
                        hideShareButton()

                    } else {
                        var message = ""
                        if (it.has("message")) {
                            message = it.get("message").asString
                        } else {
                            message = getString(R.string.msg_common_error)
                        }

                        Utils.showToast(this, message)
                    }


                })


    }



    fun storeImage(image: Bitmap) {
        val mediaStorageDir = File(filesDir, "doctordesh" + File.separator + "images")
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return
            }
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mImageName = "PT_$timeStamp.jpg"
        val pictureFile = File(mediaStorageDir , mImageName)

        if (pictureFile == null) {
            Utils.showToast(this, "Error creating media file, check storage permissions: ")
            return
        }
        try {
            var fos = FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()

            convertToPDF(pictureFile)
        } catch (e: FileNotFoundException) {

            Utils.showToast(this, "File not found: " + e.message)

        } catch (e: IOException) {

            Utils.showToast(this, "Error accessing file: " + e.message)
        }
    }






}