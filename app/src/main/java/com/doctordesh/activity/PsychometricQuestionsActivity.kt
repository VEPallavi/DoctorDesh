package com.doctordesh.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctordesh.R
import com.doctordesh.adapters.AssesmentsAdapter
import com.doctordesh.adapters.OnCategorySelectListener
import com.doctordesh.adapters.QuestionsCategoriesAdapter
import com.doctordesh.adapters.QuestionsListAdapter
import com.doctordesh.helpers.Utils
import com.doctordesh.models.PsychCategoryModel
import com.doctordesh.models.PsychQuestionModel
import com.doctordesh.viewModels.PsychometricQuestionsViewModel
import com.doctordesh.viewModels.PsychometricScaleViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_psychometric_questions.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class PsychometricQuestionsActivity : AppCompatActivity() {

    var psychCategoryModel: PsychCategoryModel? = null
    var psychQuestionList: List<PsychQuestionModel>? = null
    var psychometricQuestionsViewModel: PsychometricQuestionsViewModel? = null
    var questionsListAdapter: QuestionsListAdapter? = null
    var questionsCategoryList: ArrayList<PsychCategoryModel>? = null
    private var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychometric_questions)
        initView()
    }

    fun initView() {

        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        psychometricQuestionsViewModel =
            ViewModelProviders.of(this).get(PsychometricQuestionsViewModel::class.java)
        psychCategoryModel = intent.getParcelableExtra("psychCategory")

        if (psychCategoryModel!!.heading != null && psychCategoryModel!!.heading != "")
            tv_top_msg.text = psychCategoryModel!!.heading
        else
            tv_top_msg.visibility = View.GONE



        questionsCategoryList =
            intent.getBundleExtra("question").getParcelableArrayList("questionsCategoriesList")





        getQuestions(psychCategoryModel!!)


        tv_title.setOnClickListener({


            openCategoryDialog()

        })

        btn_score.setOnClickListener({


            openScoreDialog(psychQuestionList!!)

        })


    }

    fun getQuestions(psychCategoryModel: PsychCategoryModel) {

        shimmer_view_container.visibility = View.VISIBLE
        shimmer_view_container.startShimmer()
        tv_title.text = psychCategoryModel!!.category_name
        cl_data.visibility = View.GONE

        psychometricQuestionsViewModel!!.getPsychometricQuestions(this, psychCategoryModel!!._id)
            .observe(this,
                Observer {

                    shimmer_view_container.stopShimmer()
                    shimmer_view_container.visibility = View.GONE

                    cl_data.visibility = View.VISIBLE


                    if (it != null && it.has("status") && it.get("status").asString.equals("1")) {
                        Utils.showLog("Psych categories response : " + it.toString())
                        val type = object : TypeToken<List<PsychQuestionModel>>() {}.type
                        psychQuestionList =
                            Gson().fromJson<List<PsychQuestionModel>>(
                                it.get("payload").toString(),
                                type
                            )


                        if (psychQuestionList != null && psychQuestionList!!.size > 0) {
                            rv_questions_list.setHasFixedSize(true)
                            rv_questions_list.layoutManager = LinearLayoutManager(this)

                            for (i in psychQuestionList!!.indices) {
                                var optionList =
                                    psychQuestionList!![i].options.filter { (option) -> (option != null && option!!.isNotEmpty()) }


                                psychQuestionList!![i].options = optionList;

                                psychQuestionList!![i].selectedOption = -1
                            }


                            questionsListAdapter =
                                QuestionsListAdapter(this, psychQuestionList!!, object :
                                    OnQuestionAnswered {

                                    override fun onAnswer(pos: Int, selectedOption: Int) {

                                        psychQuestionList!!.get(pos).selectedOption = selectedOption

                                        checkAnsweredQuestions()
                                    }

                                })
                            rv_questions_list.adapter = questionsListAdapter



                            rv_questions_list.visibility = View.VISIBLE

                        } else {

                            rv_questions_list.visibility = View.GONE
                        }


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

    fun checkAnsweredQuestions() {
        var unAnsweredQuestion = 0

        for (i in psychQuestionList!!.indices) {
            if (psychQuestionList!![i].selectedOption == -1) {
                unAnsweredQuestion = 1
                break
            }
        }


        if (unAnsweredQuestion == 0) {
            btn_score.visibility = View.VISIBLE
        } else {
            btn_score.visibility = View.GONE
        }
    }


    fun openScoreDialog(psychQuestionList: List<PsychQuestionModel>) {
        var dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_open_score)
        dialog!!.show()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        var totalScore = 0
        //var answerSheet: JSONArray? = null

        var answerSheet: ArrayList<JSONObject>? = null


        for (i in psychQuestionList.indices) {

            /*if (answerSheet == null)
                answerSheet = JSONArray()*/
            if (answerSheet == null)
                answerSheet = ArrayList()


            //var item=QuestAns(psychQuestionList[i].questions!!,psychQuestionList[i].options[psychQuestionList[i].selectedOption].option!!)

            var item = JSONObject()
            item.put("question", psychQuestionList[i].questions)
            item.put(
                "answer",
                psychQuestionList[i].options[psychQuestionList[i].selectedOption].option
            )
            item.put(
                "marks",
                psychQuestionList[i].options[psychQuestionList[i].selectedOption].option_marks
            )
            answerSheet.add(item)

            totalScore += psychQuestionList[i].options[psychQuestionList[i].selectedOption].option_marks!!.toInt()
        }


        var tvScore = dialog!!.findViewById<TextView>(R.id.tv_score)
        tvScore.text = totalScore.toString()


        var tvSeverity = dialog!!.findViewById<TextView>(R.id.tv_severity)
        tvSeverity.text = Utils.getPsychometricSeverity(
            psychCategoryModel!!.category_name.toLowerCase(),
            totalScore
        )

        var etPatientDob = dialog!!.findViewById<TextView>(R.id.et_patient_dob)


        var tvCancel = dialog!!.findViewById<TextView>(R.id.tv_cancel)
        tvCancel.setOnClickListener {

            dialog.dismiss()
        }
        var etPatientName = dialog!!.findViewById<EditText>(R.id.et_patient_name)


        var tvSubmit = dialog!!.findViewById<TextView>(R.id.tv_submit)

        tvSubmit.setOnClickListener {

            if (etPatientName.text.toString().isEmpty()) {
                etPatientName.setError("Please enter patient name")
            } else if (etPatientDob.text.toString().isEmpty()) {
                etPatientDob.setError("Please enter patient DOB")
            } else {


                psychometricQuestionsViewModel!!.sendPsychometricResult(
                    this,
                    psychCategoryModel!!._id,
                    etPatientName.text.toString(),
                    totalScore.toString(),
                    etPatientDob.text.toString(),
                    answerSheet!!
                ).observe(this, Observer {

                    if (it != null && it.has("status") && it.get("status").asString.equals("1")) {

                        var message = ""
                        if (it.has("message")) {
                            message = it.get("message").asString
                        } else {

                            message = "Patient data is submitted successfully"
                        }



                        Utils.showToast(this@PsychometricQuestionsActivity, message)


                        Handler().postDelayed(Runnable {

                            finish()

                        }, 3000)


                    }


                })
                dialog.dismiss()
            }


        }


    }

    fun openDatePicker(textView: TextView) {


        var selectedDate = ""
        val mCalendar = Calendar.getInstance()
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
        mYear = mCalendar.get(Calendar.YEAR)


        var datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                run {


                    selectedDate = "" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + mYear
                    textView.text = selectedDate
                    textView.setError(null)

                }
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())

        datePickerDialog.show()


    }

    fun openCategoryDialog() {
        var dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dialog_psychometric_scales)
        dialog!!.show()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var rvPsychometricScales = dialog.findViewById<RecyclerView>(R.id.rv_psychometric_scales)
        rvPsychometricScales.setHasFixedSize(true)
        rvPsychometricScales.layoutManager = LinearLayoutManager(this)
        rvPsychometricScales.adapter = QuestionsCategoriesAdapter(
            this,
            questionsCategoryList!!,
            object : OnCategorySelectListener {
                override fun onCategorySelect(selectedCategory: PsychCategoryModel) {

                    getQuestions(selectedCategory)

                    dialog.cancel()
                }
            })

        var ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener {

            dialog.cancel()

        }


    }


}

interface OnQuestionAnswered {
    fun onAnswer(position: Int, selectedOption: Int)
}


data class QuestAns(val question: String, val answer: String)








