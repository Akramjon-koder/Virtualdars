package info.texnoman.virtualdars.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import info.texnoman.virtualdars.R

class SearchActivity2 : AppCompatActivity() {
    lateinit var ivBack: ImageView
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var searchView: androidx.appcompat.widget.SearchView
    var names = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()

        ivBack.setOnClickListener {
            finish()
        }

        names.add("ph")
        names.add("java")
        names.add("super")
        names.add("t")

        adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(applicationContext, names[position], Toast.LENGTH_SHORT).show()

            val intent = Intent(applicationContext, SearchCoursesActivity::class.java)
            intent.putExtra("query", names[position])
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                Toast.makeText(applicationContext, query, Toast.LENGTH_SHORT).show()

                val intent = Intent(applicationContext, SearchCoursesActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (names.size != 0) {
                    adapter.filter.filter(newText)
                    Toast.makeText(applicationContext, newText, Toast.LENGTH_SHORT).show()
                }
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
}