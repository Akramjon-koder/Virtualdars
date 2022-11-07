package info.texnoman.virtualdars.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import info.texnoman.virtualdars.R
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_wishlist, R.id.navigation_cart, R.id.navigation_account))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Paper.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemLogin -> {
                Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.itemSign -> {
                Toast.makeText(applicationContext, "Sign up", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.itemProfile -> {
                Toast.makeText(applicationContext, "Profile", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.itemSearch -> {
//                val user = Paper.book().read<User>("user")
                val token = Paper.book().read<String>("token")
                if (token != null) {
                    Toast.makeText(applicationContext, token, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Foydalanuvchi topilmadi", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.itemExit -> {
                Paper.book().delete("firstName")
                Paper.book().delete("lastName")
                Paper.book().delete("token")
                Paper.book().delete("tel")
                Paper.book().delete("password")
                Toast.makeText(applicationContext, "O'chirildi", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}