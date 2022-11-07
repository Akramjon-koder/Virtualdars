package info.texnoman.virtualdars.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.AllCourse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    lateinit var ivBack: ImageView
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var searchView: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()
        getAllCourses()

        ivBack.setOnClickListener {
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getSearchCourses(query)

                val intent = Intent(applicationContext, SearchCoursesActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                getSearchCourses(newText)
                return false
            }
        })
    }

    private fun initView() {
        ivBack = findViewById(R.id.ivBack)
        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        searchView.isIconified = false
    }

    private fun getAllCourses() {
        RetrofitClient.instance.getAllCourses().enqueue(object: Callback<AllCourse> {
            override fun onResponse(call: Call<AllCourse>, response: Response<AllCourse>) {
                if (response.isSuccessful) {
                    val searchCourses = response.body()

                    if (searchCourses!!.success) {
                        val names = mutableListOf<String>()
                        for (i in searchCourses.data) {
                            names.add(i.title)
                        }

                        adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, names)
                        listView.adapter = adapter

                        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            val intent = Intent(applicationContext, SearchCoursesActivity::class.java)
                            intent.putExtra("query", names[position])
                            startActivity(intent)
                        }

                    } else {
                        Toast.makeText(applicationContext, searchCourses.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AllCourse>, t: Throwable) {

            }
        })
    }

    private fun getSearchCourses(query: String) {
        RetrofitClient.instance.getSearchCourses(query).enqueue(object: Callback<AllCourse> {
            override fun onResponse(call: Call<AllCourse>, response: Response<AllCourse>) {
                if (response.isSuccessful) {
                    val searchCourses = response.body()

                    if (searchCourses!!.success) {
                        val names = mutableListOf<String>()
                        for (i in searchCourses.data) {
                            names.add(i.title)
                        }

                        adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, names)
                        listView.adapter = adapter

                        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            val intent = Intent(applicationContext, SearchCoursesActivity::class.java)
                            intent.putExtra("query", names[position])
                            startActivity(intent)
                        }

                    } else {
                        Toast.makeText(applicationContext, searchCourses.message, Toast.LENGTH_SHORT).show()
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