package info.texnoman.virtualdars.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.PasswordResetResponse
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordResetActivity : AppCompatActivity() {
    lateinit var llContent: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var btnResetPassword: Button
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var etTel: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        initView()

        btnResetPassword.setOnClickListener {
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val tel = etTel.text.toString().trim()

            checkData(password, confirmPassword, tel)
        }
    }

    private fun checkData(password: String, confirmPassword: String, tel: String) {
        when {
            password.isEmpty() -> {
                Toast.makeText(applicationContext, "Parolni kiriting", Toast.LENGTH_SHORT).show()
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(applicationContext, "Parolni qayta kiriting", Toast.LENGTH_SHORT).show()
            }
            tel.isEmpty() -> {
                Toast.makeText(applicationContext, "Telefon nomernikiriting", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                Toast.makeText(applicationContext, "Parol bir xilligini tekshiring", Toast.LENGTH_SHORT).show()
            }
            password.length < 5 || confirmPassword.length < 5 -> {
                Toast.makeText(applicationContext, "Kamida 5ta belgidan iborat parol kiriting", Toast.LENGTH_SHORT).show()
            }
            else -> {
                uploadData(password, tel)
            }
        }
    }

    private fun uploadData(password: String, tel: String) {
        llContent.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance.passwordReset(tel).enqueue(object : Callback<PasswordResetResponse> {
            override fun onResponse(call: Call<PasswordResetResponse>, response: Response<PasswordResetResponse>) {
                if (response.isSuccessful) {
                    val passwordResetResponse = response.body()

                    if (passwordResetResponse!!.success) {
                        Toast.makeText(applicationContext, passwordResetResponse.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(applicationContext, ResetVerificationActivity::class.java)
                        intent.putExtra("tel", tel)
                        intent.putExtra("password", password)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, passwordResetResponse.message, Toast.LENGTH_SHORT).show()
                        llContent.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    llContent.isVisible = true
                    lottieAnimationView.isVisible = false
                }
            }

            override fun onFailure(call: Call<PasswordResetResponse>, t: Throwable) {
                llContent.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }

    private fun initView() {
        llContent = findViewById(R.id.llContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        etPassword = findViewById(R.id.etPassword)
        etTel = findViewById(R.id.etTel)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }
}