package com.doctordesh.network

import com.doctordesh.activity.QuestAns
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*


interface ApiService {


    @POST(ApiList.SIGNUP_URL)
    @FormUrlEncoded
    fun signupUser(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("credentials") credentials: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<JsonObject>

    @POST(ApiList.VERIFY_OTP_URL)
    @FormUrlEncoded
    fun verifySignupUser(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("credentials") credentials: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("otp") otp: String

    ): Call<JsonObject>

    @GET(ApiList.GET_ALL_PROVIDERS_URL)
    fun getAllProviders(): Call<JsonObject>

    @GET(ApiList.GET_ALL_CONTACT_USER_LIST_URL)
    fun getAllContactUserList(): Call<JsonObject>



    @GET(ApiList.GET_PSYCH_CATEGORIES_URL)
    fun getPsychCategories(@Header("x-auth") token: String): Call<JsonObject>

    @POST(ApiList.GET_PSYCH_QUESTIONS_URL)
    @FormUrlEncoded
    fun getPsychQuestions(
        @Header("x-auth") token: String,
        @Field("category_id") category_id: String
    ): Call<JsonObject>


    @POST(ApiList.GET_STAFF_MEMBERS_URL)
    @FormUrlEncoded
    fun getStaffMembers(
        @Header("x-auth") token: String,
        @Field("provider_status") status: String
    ): Call<JsonObject>


    @POST(ApiList.GET_PROVIDER_SCHEDULE_URL)
    @FormUrlEncoded
    fun providerSchedule(
        @Header("x-auth") token: String,
        @Field("doc_id") id: String
    ): Call<JsonObject>


    @POST(ApiList.LOGIN_URL)
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<JsonObject>


    @POST(ApiList.FORGOT_PASS_URL)
    @FormUrlEncoded
    fun forgotPassword(
        @Field("email") email: String
    ): Call<JsonObject>


    @POST(ApiList.UPDATE_PASS_URL)
    @FormUrlEncoded
    fun updatePassword(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<JsonObject>


    @POST(ApiList.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    fun updateProfile(
        @Header("x-auth") token: String, @FieldMap params: HashMap<String, String>
    ): Call<JsonObject>


    @Multipart
    @POST(ApiList.UPDATE_PROFILE_PIC_URL)
    fun updateProfilePic(
        @Header("x-auth") token: String,
        @Part("user_id") userId: RequestBody,
        @Part profilePic: MultipartBody.Part
    ): Call<JsonObject>


    @Multipart
    @POST(ApiList.SENT_PATIENT_CONSENT_URL)
    fun sentPatientConsent(
        @Header("x-auth") token: String,
        @Part("user_id") user_id: RequestBody,
        @Part("patientName") patientName: RequestBody,
        @Part("ccmConstent") ccmConstent: RequestBody,
        @Part("bhiService") bhiService: RequestBody,
        @Part("facilityName") facilityName: RequestBody,
        @Part("roomNo") roomNo: RequestBody,
        @Part("verbalConstent") verbalConstent: RequestBody,
        @Part("careNavigator") careNavigator: RequestBody,
        @Part patientSignature: MultipartBody.Part
    ): Call<JsonObject>


    @Multipart
    @POST(ApiList.SEND_PATIENT_DOCUMENT_URL)
    fun sendPatientDocument(
        @Header("x-auth") token: String,
        @Part("provider_id") provider_id: RequestBody,
        @Part("patient_name") patient_name: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part patientDoc: ArrayList<MultipartBody.Part>
    ): Call<JsonObject>

    @Multipart
    @POST(ApiList.EMPLOYEE_RECEIPTS_URL)
    fun sendEmployeeReceipts(
        @Header("x-auth") token: String,
        @Part("provider_id") provider_id: RequestBody,
        @Part("employee_name") patient_name: RequestBody,
        @Part("type") type: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part reciept: ArrayList<MultipartBody.Part>
    ): Call<JsonObject>



    @Multipart
    @POST(ApiList.UPDATE_PATIENT_REFILL_PIC_URL)
    fun updatePatientPic(
        @Header("x-auth") token: String,
        @Part("filename") filename: RequestBody,
        @Part("patientId") patientID: RequestBody,
        @Part profilePic: MultipartBody.Part
    ): Call<JsonObject>


    @GET(ApiList.GET_CREDENTIALS_URL)
    fun getCredentials(): Call<JsonObject>


    @POST(ApiList.SENT_PATIENT_REFERAL_URL)
    @FormUrlEncoded
    fun submitPatientReferal(
        @Header("x-auth") token: String,
        @Field("patient_name") patient_name: String,
        @Field("facility_name") facility_name: String,
        @Field("room_number") room_number: String,
        @Field("reason") reason: String,
        @Field("user_id") user_id: String,
        @Field("refferal_type") refferal_type: String

    ): Call<JsonObject>


    @POST(ApiList.REQUEST_MED_REFILL_URL)
    @FormUrlEncoded
    fun submitMedRquestRefill(
        @Header("x-auth") token: String,
        @Field("patient_name") patient_name: String,
        @Field("patient_dob") patient_dob: String,
        @Field("patient_location") patient_location: String,
        @Field("name_of_medicine") name_of_medicine: String,
        @Field("dose_of_medicine") dose_of_medicine: String,

        @Field("frequency_dose") frequency_dose: String,
        @Field("pharmacy_name") pharmacy_name: String,
        @Field("pharmacy_fax_no") pharmacy_fax_no: String,

        @Field("pharmacy_phone_number") pharmacy_phone_number: String,
        @Field("yourfaxNumber") yourfaxNumber: String,
        @Field("prn_text") prn_text: String,

        @Field("user_id") user_id: String


    ): Call<JsonObject>


    @POST(ApiList.GET_PROVIDERS_URL)
    @FormUrlEncoded
    fun getProviderList(
        @Header("x-auth") token: String,
        @Field("provider_status") provider_status: IntArray
    ): Call<JsonObject>



    @POST(ApiList.INVITE_VIDEO_CALLING_URL)
    @FormUrlEncoded
    fun inviteUser(
        @Header("x-auth") token: String,
        @Field("callee_id") calleeId: String,
        @Field("alert_message") alert_message: String,
        @Field("title") title: String
    ): Call<JsonObject>

    @POST(ApiList.REJECT_VIDEO_CALLING_URL)
    @FormUrlEncoded
    fun rejectVideoCall(
        @Header("x-auth") token: String,
        @Field("callee_id") calleeId: String,
        @Field("alert_message") alert_message: String,
        @Field("title") title: String
    ): Call<JsonObject>

    @POST(ApiList.GET_NOTIFICATIONS_URL)
    @FormUrlEncoded
    fun getNotifications(
        @Header("x-auth") token: String,
        @Field("user_id") user_id: String

    ): Call<JsonObject>

    @POST(ApiList.SEND_NOTIFICATION_URL)
    @FormUrlEncoded
    fun sendNotification(
        @Header("x-auth") authToken: String,
        @Field("token") token: String,
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("user_id") user_id: String

    ): Call<JsonObject>


    @POST(ApiList.SUBMIT_PSYCH_ANSWERS_RESULT_URL)
    @FormUrlEncoded
    fun submitPsychAnsResult(
        @Header("x-auth") token: String,
        @Field("doc_id") user_id: String,
        @Field("category_id") category_id: String,
        @Field("patientName") patientName: String,
        @Field("marks") marks: String,
        @Field("dob") dob: String,
        @Field("answers") answers: ArrayList<JSONObject>
    ): Call<JsonObject>





    @GET(ApiList.GET_MEDITATION_SURVEY_URL)
    fun getMeditationData(
        @Header("x-auth") token: String
    ): Call<JsonObject>

    @GET
    fun getSurveyList(
        @Url url: String,
        @Header("x-auth") token: String
    ): Call<JsonObject>


    @GET
    fun getVideosList(
        @Url url:String,
        @Header("x-auth") token: String
    ): Call<JsonObject>

    @GET
    fun getPsychMaterial(
        @Url url:String,
        @Header("x-auth") token: String
    ): Call<JsonObject>




}