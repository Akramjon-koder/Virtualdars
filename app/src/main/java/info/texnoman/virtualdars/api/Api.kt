package info.texnoman.virtualdars.api

import info.texnoman.virtualdars.model.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("/mbapp/auth/login")
    fun loginData(
        @Field("username") tel: String,
        @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("/mbapp/signup/register")
    fun signData(
            @Field("firstname") firsName: String,
            @Field("lastname") lastName: String,
            @Field("phone_number") tel: String,
            @Field("password") password: String): Call<SignResponse>

    @FormUrlEncoded
    @POST("/mbapp/signup/verify")
    fun signUpVerification(@Field("code") code: String,
                           @Field("phone_number") tel: String): Call<SignUpVerificationResponse>

    @FormUrlEncoded
    @POST("/mbapp/reset/index/")
    fun passwordReset(@Field("phone") tel: String): Call<PasswordResetResponse>

    @FormUrlEncoded
    @POST("/mbapp/reset/check-code/")
    fun resetVerification(@Field("code") code: String,
                          @Field("phone") tel: String,
                          @Field("password") password: String): Call<ResetVerificationResponse>

    @FormUrlEncoded
    @POST("/mbapp/signup/verify")
    fun sendTime(@Field("id") id: Int): Call<TimeResponse>

    @POST("/mbapp/start2/index")
    fun getMyCourseLesson(@Query("id") id: Int): Call<Lesson>

    @GET("/mbapp/start2/lesson")
    fun getLessonTheme(@Query("id") id: Int): Call<Lesson>

    @POST("/mbapp/user/my-favourite-courses")
    fun getWishlist(): Call<Wishlist>

    @POST("/mbapp/user/my-courses")
    fun getMyCourses(): Call<MyCourse>

    @GET("/mbapp/course/popular-courses")
    fun getTopCourses(): Call<TopCourse>

    @GET("/mbapp/course/all")
    fun getAllCourses(): Call<AllCourse>

    @GET("/mbapp/course/all")
    fun getSearchCourses(@Query("search") query: String): Call<AllCourse>

    @GET("/mbapp/category/all")
    fun getCategories(): Call<List<Category>>

    @GET("/mbapp/category/courses")
    fun getCategoryCourses(@Query("id") id: Int): Call<List<Course>>

    @GET("/mbapp/course/detail/")
    fun getDetail(@Query("id") id: Int): Call<Detail>

    @POST("/mbapp/user/wish")
    fun isWish(@Query("id") id: Int): Call<IsWish>

    @FormUrlEncoded
    @POST("/mbapp/user/change-password")
    fun changePassword(@Field("current_password") currentPassword: String,
                       @Field("password") newPassword: String): Call<ChangePasswordResponse>

    @FormUrlEncoded
    @POST("/mbapp/user/update-personal-data")
    fun editProfile(@Field("firstname") firstName: String,
                    @Field("lastname") lastName: String): Call<EditProfileResponse>

    @POST("/mbapp/user/check-token")
    fun checkToken(): Call<CheckToken>

    @FormUrlEncoded
    @POST("/mbapp/watch/lesson/")
    fun sendRequest(@Field("lessonId") lessonId: String): Call<SendRequestResponse>

    @GET("/mbapp/course/offline-payment")
    fun buyCourse(): Call<BuyCourse>

    @FormUrlEncoded
    @POST("/mbapp/user/free-enroll")
    fun subscribeCourse(@Field("course_id") courseId: Int): Call<SubscribeResponse>

    @FormUrlEncoded
    @POST("/mbapp/lesson/files")
    fun fileDownload(@Field("lesson_id") lessonId: Int): Call<FileResponse>
    @FormUrlEncoded
    @POST("/mbapp/user/buy-course")
    fun getTransactionParams(@Query("lang") language:String,@Field("course_id") course_id:Int):Call<TransactionModel>
}