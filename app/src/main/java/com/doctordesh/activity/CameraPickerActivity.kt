package com.doctordesh.activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.doctordesh.R
import kotlinx.android.synthetic.main.activity_camera_picker.*
import java.io.File
import java.util.concurrent.Executors

class CameraPickerActivity : AppCompatActivity() {


      val REQUEST_CODE_PERMISSIONS = 10



    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_picker)


        if(allPermissionsGranted())
        {
            view_provider.post { startCamera() }
        }


    }


    private val executor = Executors.newSingleThreadExecutor()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if(allPermissionsGranted())
        {
            view_provider.post { startCamera() }
        }
        else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        // Every time the provided texture view changes, recompute layout
        view_provider.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }



    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun startCamera()
    {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)




        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = view_provider.parent as ViewGroup
            parent.removeView(view_provider)
            parent.addView(view_provider, 0)

            view_provider.surfaceTexture = it.surfaceTexture
            updateTransform()
        }




        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()


        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)







        iv_camera_click.setOnClickListener(View.OnClickListener {


            val file = File(externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        view_provider.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        view_provider.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                            openImagePreviewDialog(file.absolutePath);

                        }
                    }
                })


        })

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview,imageCapture)




    }


    private fun openImagePreviewDialog(imagePath:String)
    {

       var imagePreviewDialog=Dialog(this)

        imagePreviewDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        imagePreviewDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        imagePreviewDialog!!.setContentView(R.layout.dialog_image_preview)
        imagePreviewDialog!!.show()
        imagePreviewDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        imagePreviewDialog.show()



        var ivImagePreview=imagePreviewDialog.findViewById<ImageView>(R.id.iv_image_preview)

        Glide.with(this)
            .load(imagePath)
            .apply(RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
            .into(ivImagePreview)



        var ivOk=imagePreviewDialog.findViewById<ImageView>(R.id.iv_ok)
        var ivCancel=imagePreviewDialog.findViewById<ImageView>(R.id.iv_cancel)


        ivOk.setOnClickListener(View.OnClickListener {

            imagePreviewDialog.dismiss()

        })


        ivCancel.setOnClickListener(View.OnClickListener {

            imagePreviewDialog.dismiss()

        })






    }


    private fun updateTransform() {

        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = view_provider.width / 2f
        val centerY = view_provider.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(view_provider.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        view_provider.setTransform(matrix)


    }


}
