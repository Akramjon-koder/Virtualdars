package info.texnoman.virtualdars.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.SectionAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.Detail
import info.texnoman.virtualdars.model.DetailData
import info.texnoman.virtualdars.model.IsWish
import info.texnoman.virtualdars.model.SubscribeResponse
import info.texnoman.virtualdars.utils.Utils
import io.paperdb.Paper
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CourseActivity : AppCompatActivity() {
    lateinit var tvRatingValue: TextView
    lateinit var tvRatingUsers: TextView
    lateinit var ratingBar: RatingBar
    lateinit var rlContent: RelativeLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var ibPlayVideo: ImageButton
    lateinit var btnBuyCourse: Button
    lateinit var ivBack: ImageView
    lateinit var ivShare: ImageView
    lateinit var ivBanner: ImageView
    lateinit var ivWish: ImageView
    lateinit var tvCourseInclude: TextView
    lateinit var tvCourseOutcomes: TextView
    lateinit var tvCourseRequirement: TextView
    lateinit var tvPrice: TextView
    lateinit var tvTitle: TextView
    lateinit var tvStudentCount: TextView
    lateinit var rvCourseSection: RecyclerView
    lateinit var detailData: DetailData
    var video_url: String = ""
    var youtube_video: Boolean = false
    companion object
    {
        lateinit var data:DetailData
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        val id = Objects.requireNonNull(intent.extras)!!.getInt("id")

        initView()
        setSection(id)

        ivBack.setOnClickListener {
            finish()
        }

        ivWish.setOnClickListener {
            isWish(id)
        }

        ivShare.setOnClickListener {
            isShare()
        }

        btnBuyCourse.setOnClickListener {
            if (detailData.isEnrolled) {
                Toast.makeText(applicationContext, "Sizda ushbu kurs mavjud", Toast.LENGTH_SHORT).show()
            } else {
                if (detailData.is_free == 0) {

                    val token = Paper.book().read<String>("token","")
                    if(token.isNotEmpty()){
                    var intent  =Intent(applicationContext,BuyCourseActivity::class.java)
                        data =detailData
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Ro'yxatdan o'ting", Toast.LENGTH_SHORT).show()
                    }


                //  startActivity(Intent(applicationContext, ))
                } else {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setMessage("Siz ushbu kursga a'zo bo'lmoqchimisiz?")
                    builder.setPositiveButton("A'zo bo'lish") { _, _ ->
                        subscribeCourse()
                    }
                    builder.setNegativeButton("Qaytish") { dialog, _ ->
                        dialog.cancel()
                    }

                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }

        ibPlayVideo.setOnClickListener {
            if (video_url == "") {
                Toast.makeText(applicationContext, "Video topilmadi", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(applicationContext, PlayVideoActivity::class.java)
                intent.putExtra("youtube_video", youtube_video)
                intent.putExtra("video_url", video_url)
                startActivity(intent)
//                if (youtube_video) {
//                    val intent = Intent(Intent.ACTION_VIEW,
//                        Uri.parse(video_url))
//                    startActivity(intent)
//                } else {
//                    val intent = Intent(applicationContext, PlayVideoActivity::class.java)
//                    intent.putExtra("youtube_video", youtube_video)
//                    intent.putExtra("video_url", video_url)
//                    startActivity(intent)
//                }
            }
        }

        tvCourseInclude.setOnClickListener {
            val list: ArrayList<String> = ArrayList()
            for (i in detailData.includes) {
                list.add(i)
            }

            val intent = Intent(applicationContext, CourseAboutActivity::class.java)
            intent.putExtra("title", "Kurs tarkibi")
            intent.putStringArrayListExtra("courseAbout", list)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        tvCourseOutcomes.setOnClickListener {
            val list: ArrayList<String> = ArrayList()
            for (i in detailData.benefits) {
                list.add(i)
            }

            val intent = Intent(applicationContext, CourseAboutActivity::class.java)
            intent.putExtra("title", "Siz nimani o'rganasiz?")
            intent.putStringArrayListExtra("courseAbout", list)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        tvCourseRequirement.setOnClickListener {
            val list: ArrayList<String> = ArrayList()
            for (i in detailData.requirements) {
                list.add(i)
            }

            val intent = Intent(applicationContext, CourseAboutActivity::class.java)
            intent.putExtra("title", "Kurs talablari")
            intent.putStringArrayListExtra("courseAbout", list)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
    }

    private fun subscribeCourse() {
        RetrofitClient.instance_2.subscribeCourse(detailData.id).enqueue(object: Callback<SubscribeResponse> {
            override fun onResponse(call: Call<SubscribeResponse>, response: Response<SubscribeResponse>) {
                if (response.isSuccessful) {
                    val subscribeResponse = response.body()

                    if (subscribeResponse!!.success) {
                        Toast.makeText(applicationContext, subscribeResponse.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, subscribeResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SubscribeResponse>, t: Throwable) {

            }
        })
    }

    private fun initView() {
        tvRatingValue = findViewById(R.id.tvRatingValue)
        tvRatingUsers = findViewById(R.id.tvRatingUsers)
        ratingBar = findViewById(R.id.ratingBar)
        btnBuyCourse = findViewById(R.id.btnBuyCourse)
        ibPlayVideo = findViewById(R.id.ibPlayVideo)
        rlContent = findViewById(R.id.rlContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        tvPrice = findViewById(R.id.tvPrice)
        tvCourseInclude = findViewById(R.id.tvCourseInclude)
        tvCourseOutcomes = findViewById(R.id.tvCourseOutcomes)
        tvCourseRequirement = findViewById(R.id.tvCourseRequirement)
        tvTitle = findViewById(R.id.tvTitle)
        tvStudentCount = findViewById(R.id.tvStudentCount)
        ivBanner = findViewById(R.id.ivBanner)
        ivWish = findViewById(R.id.ivWish)
        ivBack = findViewById(R.id.ivBack)
        ivShare = findViewById(R.id.ivShare)

        rvCourseSection = findViewById(R.id.rvCourseSection)
        rvCourseSection.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun isShare() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plan"
        intent.putExtra(Intent.EXTRA_SUBJECT, Utils.BASE_URL + detailData.slug)
        val appUrl = ""
        intent.putExtra(Intent.EXTRA_TEXT, appUrl)
        startActivity(Intent.createChooser(intent, "Baham ko'rish..."))
    }

    private fun isWish(id: Int) {
        RetrofitClient.instance_2.isWish(id).enqueue(object: Callback<IsWish> {
                    override fun onResponse(call: Call<IsWish>, response: Response<IsWish>) {
                        if (response.isSuccessful) {
                            val isWish = response.body()

                            if (isWish!!.success) {
                                if (isWish.action == "added") {
                                    ivWish.setImageResource(R.drawable.wishlist_filled)
                                } else {
                                    ivWish.setImageResource(R.drawable.wishlist_empty)
                                }
                                Toast.makeText(applicationContext, isWish.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, isWish.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<IsWish>, t: Throwable) {

                    }
                })
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

    private fun setSection(id: Int) {
        RetrofitClient.instance_2.getDetail(id).enqueue(object : Callback<Detail> {
                override fun onResponse(call: Call<Detail>, response: Response<Detail>) {
                    if (response.isSuccessful) {
                        val detail = response.body()

                        if (detail!!.success) {
                            detailData = detail.data
                            video_url = detail.data.video_link
                            youtube_video = detail.data.youtube

                            val adapter = SectionAdapter(applicationContext, detailData.sections)
                            rvCourseSection.adapter = adapter

                            if (detailData.isWished) {
                                ivWish.setImageResource(R.drawable.wishlist_filled)
                            } else {
                                ivWish.setImageResource(R.drawable.wishlist_empty)
                            }

                            if (detailData.is_free == 1) {
                                btnBuyCourse.text = "A'zo bo'lish"
                            }

                            if (detailData.isEnrolled) {
                                if (detailData.is_free == 1) {
                                    btnBuyCourse.text = "A'zo bo'lingan"
                                } else {
                                    btnBuyCourse.text = "Sotib olingan"
                                }
                            }

                            ratingBar.rating = detailData.rating
                            tvRatingValue.text = detailData.rating.toString()
                            tvRatingUsers.text = "(${detailData.ratingsCount})"
                            tvTitle.text = detailData.title
                            tvPrice.text = detailData.formattedPrice
                            tvStudentCount.text = detailData.students.toString()
                            Glide.with(applicationContext!!).load("https://virtualdars.uz" + detailData.image).centerCrop().into(ivBanner)

                            rlContent.isVisible = true
                            lottieAnimationView.isVisible = false

                        } else {
                            Toast.makeText(applicationContext, detail.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Detail>, t: Throwable) {

                }
            })
    }
}