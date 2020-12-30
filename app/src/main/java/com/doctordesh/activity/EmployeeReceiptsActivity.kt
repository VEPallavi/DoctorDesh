package com.doctordesh.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.doctordesh.R
import com.doctordesh.viewModels.EmployeeReceiptsViewModel
import kotlinx.android.synthetic.main.activity_employee_receipt.*
import java.io.File

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

    }


}