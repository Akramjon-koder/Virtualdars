package info.texnoman.virtualdars.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.CourseAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.AllCourse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchCoursesActivity : AppCompatActivity() {
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var rvSearchCourses: RecyclerView
    lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_courses)

        val query = Objects.requireNonNull(intent.extras)!!.getString("query").toString()

        initView()
        getAllCourse(query)

        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        rvSearchCourses = findViewById(R.id.rvSearchCourses)
        rvSearchCourses.setHasFixedSize(true)
        rvSearchCourses.layoutManager = LinearLayoutManager(applicationContext)

        ivBack = findViewById(R.id.ivBack)
    }

    private fun getAllCourse(query: String) {
        RetrofitClient.instance.getSearchCourses(query).enqueue(object: Callback<AllCourse> {
            override fun onResponse(call: Call<AllCourse>, response: Response<AllCourse>) {
                if (response.isSuccessful) {
                    val searcCourses = response.body()

                    if (searcCourses!!.success) {
                        val adapter = CourseAdapter(applicationContext, searcCourses.data)
                        rvSearchCourses.adapter = adapter

                        lottieAnimationView.isVisible = false
                    } else {
                        Toast.makeText(applicationContext, searcCourses.message, Toast.LENGTH_SHORT).show()
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