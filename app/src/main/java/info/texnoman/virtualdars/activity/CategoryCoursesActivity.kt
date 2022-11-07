package info.texnoman.virtualdars.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.CourseAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.Course
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CategoryCoursesActivity : AppCompatActivity() {
    lateinit var rvCategoryCourses: RecyclerView
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var nsvContent: NestedScrollView
    lateinit var tvTitle: TextView
    lateinit var ivBack: ImageView
    lateinit var ivSearch: ImageView
    var id: Int = 0
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_courses)

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
        id = Objects.requireNonNull(intent.extras)!!.getInt("id")
        name = Objects.requireNonNull(intent.extras)!!.getString("name").toString()

        nsvContent = findViewById(R.id.nsvContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        rvCategoryCourses = findViewById(R.id.rvCategoryCourses)
        rvCategoryCourses.setHasFixedSize(true)
        rvCategoryCourses.layoutManager = LinearLayoutManager(applicationContext)

        ivSearch = findViewById(R.id.ivSearch)
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitle.text = name
    }

    private fun getAllCourse() {
        RetrofitClient.instance.getCategoryCourses(id).enqueue(object : Callback<List<Course>> {
                override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                    if (response.isSuccessful) {
                        val courses = response.body() as ArrayList<Course>
                        val adapter = CourseAdapter(applicationContext, courses)
                        rvCategoryCourses.adapter = adapter

                        nsvContent.isVisible = true
                        lottieAnimationView.isVisible = false
                    } else {
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Course>>, t: Throwable) {

                }
            })
    }
}