package info.texnoman.virtualdars.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.LoginActivity
import info.texnoman.virtualdars.activity.SearchActivity
import info.texnoman.virtualdars.adapter.MyCourseAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.*
import io.paperdb.Paper
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MyCourseFragment : Fragment() {
    lateinit var llEmptyMyCourses: LinearLayout
    lateinit var rlSignPlaceholder: RelativeLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var btnLogin: Button
    lateinit var ivSearch: ImageView
    lateinit var rlMyCourse: RelativeLayout
    lateinit var rvMyCourse: RecyclerView

    @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_course, container, false)

        initView(root)

        ivSearch.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        val token = Paper.book().read<String>("token")
        if (token != null) {
            rlMyCourse.isVisible = true
            myCourses()
        } else {
            rlSignPlaceholder.isVisible = true
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
            CustomIntent.customType(context, "fadein-to-fadeout")
        }

        return root
    }

    private fun initView(root: View) {
        ivSearch = root.findViewById(R.id.ivSearch)
        llEmptyMyCourses = root.findViewById(R.id.llEmptyMyCourses)
        rlMyCourse = root.findViewById(R.id.rlMyCourse)
        lottieAnimationView = root.findViewById(R.id.lottieAnimationView)
        btnLogin = root.findViewById(R.id.btnLogin)
        rlSignPlaceholder = root.findViewById(R.id.rlSignPlaceholder)
        rvMyCourse = root.findViewById(R.id.rvMyCourse)
        rvMyCourse.layoutManager = LinearLayoutManager(context)
    }

    private fun myCourses() {
        RetrofitClient.instance_2.getMyCourses().enqueue(object: Callback<MyCourse> {
                    override fun onResponse(call: Call<MyCourse>, response: Response<MyCourse>) {
                        if (response.isSuccessful) {
                            val myCourse = response.body()

                            if (myCourse!!.success) {
                                val adapter = MyCourseAdapter(context, myCourse.data)
                                rvMyCourse.adapter = adapter

                                rvMyCourse.isVisible = true
                                lottieAnimationView.isVisible = false

                                if (myCourse.data.isEmpty()) {
                                    llEmptyMyCourses.isVisible = true
                                }
                            } else {
                                Toast.makeText(context, myCourse.message, Toast.LENGTH_SHORT).show()
                                rlMyCourse.isVisible = false
                                rlSignPlaceholder.isVisible = true
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                            rlMyCourse.isVisible = false
                            rlSignPlaceholder.isVisible = true
                        }
                    }

                    override fun onFailure(call: Call<MyCourse>, t: Throwable) {

                    }
                })
    }
}