package info.texnoman.virtualdars.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.LoginResponse
import io.paperdb.Paper
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var llLogin: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var etTel: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var tvSignIn: TextView
    lateinit var tvTitle: TextView
    lateinit var tvPasswordReset: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()

        btnLogin.setOnClickListener {
            val tel = etTel.text.toString().trim()
            val password = etPassword.text.toString().trim()

            checkData(tel, password)
        }

        tvSignIn.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

        tvPasswordReset.setOnClickListener {
            startActivity(Intent(applicationContext, PasswordResetActivity::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

    private fun initView() {
        llLogin = findViewById(R.id.llLogin)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        etTel = findViewById(R.id.etTel)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignIn = findViewById(R.id.tvSignIn)
        tvPasswordReset = findViewById(R.id.tvPasswordReset)
    }

    private fun checkData(tel: String, password: String) {
        when {
            tel.isEmpty() -> {
                Toast.makeText(applicationContext, "Telefon nomerni kiriting", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(applicationContext, "Parolni kiriting", Toast.LENGTH_SHORT).show()
            } else -> {
                uploadData(tel, password)
            }
        }
    }

    private fun uploadData(tel: String, password: String) {
        llLogin.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance.loginData(tel, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val model = response.body()

                    if (model!!.success) {
                        Paper.book().write("firstName", model.data.firstname)
                        Paper.book().write("lastName", model.data.lastname)
                        Paper.book().write("token", model.data.token)
                        Paper.book().write("tel", tel)
                        Paper.book().write("password", password)

                        Toast.makeText(applicationContext, model.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, model.message, Toast.LENGTH_SHORT).show()
                        llLogin.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    llLogin.isVisible = true
                    lottieAnimationView.isVisible = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                llLogin.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }
}