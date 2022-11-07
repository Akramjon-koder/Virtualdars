package info.texnoman.virtualdars.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.CourseAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.AllCourse
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoursesActivity : AppCompatActivity() {
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var nsvContent: NestedScrollView
    lateinit var rvAllCourse: RecyclerView
    lateinit var tvTitle: TextView
    lateinit var ivBack: ImageView
    lateinit var ivSearch: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        initView()
        getAllCourse()

        ivBack.setOnClickListener {
            finish()
        }

        ivSearch.setOnClickListener {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

    private fun initView() {
        nsvContent = findViewById(R.id.nsvContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        rvAllCourse = findViewById(R.id.rvAllCourse)
        rvAllCourse.setHasFixedSize(true)
        rvAllCourse.layoutManager = LinearLayoutManager(applicationContext)

        ivSearch = findViewById(R.id.ivSearch)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitle.text = "Barcha kurslar"
    }

    private fun getAllCourse() {
        RetrofitClient.instance.getAllCourses().enqueue(object: Callback<AllCourse> {
                override fun onResponse(call: Call<AllCourse>, response: Response<AllCourse>) {
                    if (response.isSuccessful) {
                        val allCourse = response.body()

                        if (allCourse!!.success) {
                            val adapter = CourseAdapter(applicationContext, allCourse.data)
                            rvAllCourse.adapter = adapter

                            nsvContent.isVisible = true
                            lottieAnimationView.isVisible = false
                        } else {
                            Toast.makeText(applicationContext, allCourse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AllCourse>, t: Throwable) {

                }
            })
    }
}