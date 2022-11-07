package info.texnoman.virtualdars.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.CourseAboutAdapter
import maes.tech.intentanim.CustomIntent
import java.util.*

class CourseAboutActivity : AppCompatActivity() {
    lateinit var tvTitle: TextView
    lateinit var ivBack: ImageView
    lateinit var rvCourseAbout: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_about)

        val title = Objects.requireNonNull(intent.extras)!!.getString("title")
        val courseAbout = Objects.requireNonNull(intent.extras)!!.getStringArrayList("courseAbout")

        initView(title)

        val adapter = CourseAboutAdapter(applicationContext, courseAbout!!)
        rvCourseAbout.adapter = adapter

        ivBack.setOnClickListener {
            finish()
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
    }

    private fun initView(title: String?) {
        tvTitle = findViewById(R.id.tvTitle)
        ivBack = findViewById(R.id.ivBack)
        rvCourseAbout = findViewById(R.id.rvCourseAbout)
        rvCourseAbout.layoutManager = LinearLayoutManager(this)

        tvTitle.text = title
    }
}