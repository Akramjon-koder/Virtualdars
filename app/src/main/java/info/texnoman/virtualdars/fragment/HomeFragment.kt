package info.texnoman.virtualdars.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.CoursesActivity
import info.texnoman.virtualdars.activity.TopCoursesActivity
import info.texnoman.virtualdars.activity.SearchActivity
import info.texnoman.virtualdars.adapter.CategoryAdapter
import info.texnoman.virtualdars.adapter.TopCourseAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.Category
import info.texnoman.virtualdars.model.TopCourse
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var rvProducts: RecyclerView
    lateinit var rvCategories: RecyclerView
    lateinit var tvAllCourse: TextView
    lateinit var tvAllTopCourse: TextView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var ivSearch: ImageView
    private var categories = mutableListOf<Category>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initView(root)

        topProducts()

        ivSearch.setOnClickListener {
            context?.startActivity(Intent(context, SearchActivity::class.java))
        }

        tvAllCourse.setOnClickListener {
            context?.startActivity(Intent(context, CoursesActivity::class.java))
            CustomIntent.customType(context, "fadein-to-fadeout")
        }

        tvAllTopCourse.setOnClickListener {
            context?.startActivity(Intent(context, TopCoursesActivity::class.java))
            CustomIntent.customType(context, "fadein-to-fadeout")
        }

        swipeRefresh.setOnRefreshListener {
            topProducts()
            swipeRefresh.isRefreshing = false
        }

        return root
    }

    private fun initView(view: View) {
        ivSearch = view.findViewById(R.id.ivSearch)
        ivSearch.isVisible = true

        tvAllCourse = view.findViewById(R.id.tvAllCourse)
        tvAllTopCourse = view.findViewById(R.id.tvAllTopCourse)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        rvProducts = view.findViewById(R.id.rvProducts)
        rvProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        rvCategories = view.findViewById(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(context)

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout)
        shimmerFrameLayout.startShimmerAnimation()
    }

    private fun topProducts() {
        RetrofitClient.instance.getTopCourses().enqueue(object : Callback<TopCourse> {
                override fun onResponse(call: Call<TopCourse>, response: Response<TopCourse>) {
                    if (response.isSuccessful) {
                        val topCourse = response.body()

                        if (topCourse!!.success) {
                            val adapter = TopCourseAdapter(context, topCourse.data)
                            rvProducts.adapter = adapter

                            categories()
                        } else {
                            Toast.makeText(context, topCourse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Xatolik", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TopCourse>, t: Throwable) {

                }
            })
    }

    private fun categories() {
        RetrofitClient.instance.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        shimmerFrameLayout.stopShimmerAnimation()
                        shimmerFrameLayout.isVisible = false

                        categories = response.body() as ArrayList<Category>

                        val adapter = CategoryAdapter(context, categories)
                        rvCategories.adapter = adapter
                    } else {
                        Toast.makeText(context, "Xatolik", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {

                }
            })
    }
}