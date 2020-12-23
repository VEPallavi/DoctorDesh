package com.doctordesh.models

data class PsychQuestionModel(
    val questions: String?,
    var selectedOption: Int = -1,
    val category_id: String?,
    var options: List<Option>

)

data class Option(val option: String?, val option_marks: String?, var isChecked: Boolean = false) {

}






