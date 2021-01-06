package com.doctordesh.network

class ApiList {
    companion object {

        //val BASE_URL = "http://ec2-34-217-28-142.us-west-2.compute.amazonaws.com:8000/"


        val BASE_URL = "http://34.215.131.33:8000/"

        const val SIGNUP_URL = "user/user_register"
        const val VERIFY_OTP_URL = "user/verify_signup"
        const val LOGIN_URL = "user/signin"
        const val FORGOT_PASS_URL = "user/forgot_password"
        const val UPDATE_PASS_URL = "user/update_password"
        const val UPDATE_PROFILE_URL = "user/update_profile"
        const val UPDATE_PROFILE_PIC_URL = "user/update_profile_pic"
        const val GET_CREDENTIALS_URL = "user/credantials?search=all"
        const val SENT_PATIENT_REFERAL_URL = "patient/patient_refferel"
        const val GET_PROVIDERS_URL = "contact/contactprovider"
        const val GET_VIDEOS_URL = "education/get_educational_videos"
        const val GET_PROVIDERS_VIDEOS_URL = "education/get_videos"
        const val INVITE_VIDEO_CALLING_URL = "contact/invite_calling"
        const val REJECT_VIDEO_CALLING_URL = "contact/reject_calling"
        const val GET_NOTIFICATIONS_URL = "user/notifications"
        const val SEND_NOTIFICATION_URL = "contact/send_message"
        const val REQUEST_MED_REFILL_URL = "patient/patient_dose"
        const val UPDATE_PATIENT_REFILL_PIC_URL = "patient/patient_image"
        const val GET_SURVEY_URL = "user/servey"
        const val GET_PROVIDER_SURVEY_URL = "user/providerServey"
        const val GET_MEDITATION_SURVEY_URL = "user/meditationServey"
        const val SENT_PATIENT_CONSENT_URL = "patientConstent/add_constent"
        const val GET_ALL_PROVIDERS_URL = "user/providers_listing"
        const val GET_PROVIDER_SCHEDULE_URL = "storeTimings/providers_timing"
        const val GET_STAFF_MEMBERS_URL = "contact/contactStaff"
        const val GET_PSYCH_CATEGORIES_URL = "questionsCategories/get_question_caegories"
        const val GET_PSYCH_QUESTIONS_URL = "psychometricQuestions/get_psychometricQuestions"
        const val GET_PSYCH_MED_URL = "user/get_psych_medical_videos"
        const val GET_PSYCH_DISORDERS_URL = "user/get_psych_disorder_videos"
        const val SUBMIT_PSYCH_ANSWERS_RESULT_URL = "answersSubmition/submit_answers"

        const val GET_SURVEYS_FROM_STAFF_URL = "user/staffServey"
        const val SEND_PATIENT_DOCUMENT_URL = "patientDocument/add_patientDocument"
        const val EMPLOYEE_RECEIPTS_URL = "employeeReciept/add_employeeReciept"

        var ABOUT_US_URL = BASE_URL + "user/about"
        var TERMS_CONDITION_URL = BASE_URL + "user/terms-condition"
        var VIDEO_CALL_INSTRUCTIONS_URL = BASE_URL + "user/video-instructions"
        var MAIN_OFFICE_URL = BASE_URL + "user/contact_mainOffice"


    }
}