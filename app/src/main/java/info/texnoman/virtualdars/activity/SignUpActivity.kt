package info.texnoman.virtualdars.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.SignResponse
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    lateinit var llSignUp: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var tvTitle: TextView
    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    lateinit var etTel: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initView()

        btnLogin.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val tel = etTel.text.toString().trim()
            val password = etPassword.text.toString().trim()

            checkData(firstName, lastName, tel, password)
        }
    }

    private fun checkData(firstName: String, lastName: String, tel: String, password: String) {
        when {
            firstName.isEmpty() -> {
                Toast.makeText(applicationContext, "Ismni kiriting", Toast.LENGTH_SHORT).show()
            }
            lastName.isEmpty() -> {
                Toast.makeText(applicationContext, "Familyani kiriting", Toast.LENGTH_SHORT).show()
            }
            tel.isEmpty() -> {
                Toast.makeText(applicationContext, "Telefon nomerni kiriting", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(applicationContext, "Parolni kiriting", Toast.LENGTH_SHORT).show()
            }
            password.length < 5 -> {
                Toast.makeText(applicationContext, "Kamida 5ta belgidan iborat parol kiriting", Toast.LENGTH_SHORT).show()
            }
            else -> {
                uploadData(firstName, lastName, tel, password)
            }
        }
    }

    private fun uploadData(firstName: String, lastName: String, tel: String, password: String) {
        llSignUp.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance.signData(firstName, lastName, tel, password)
                .enqueue(object: Callback<SignResponse> {
                    override fun onResponse(call: Call<SignResponse>, response: Response<SignResponse>) {
                        if (response.isSuccessful) {
                            val signResponse = response.body()

                            if (signResponse!!.success) {
                                Toast.makeText(applicationContext, signResponse.message, Toast.LENGTH_SHORT).show()

                                val intent = Intent(applicationContext, SignUpVerificationActivity::class.java)
                                intent.putExtra("firstName", firstName)
                                intent.putExtra("lastName", lastName)
                                intent.putExtra("tel", tel)
                                intent.putExtra("password", password)
                                startActivity(intent)
                            } else {
                                Toast.makeText(applicationContext, signResponse.message, Toast.LENGTH_SHORT).show()
                                llSignUp.isVisible = true
                                lottieAnimationView.isVisible = false
                            }
                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                            llSignUp.isVisible = true
                            lottieAnimationView.isVisible = false
                        }
                    }

                    override fun onFailure(call: Call<SignResponse>, t: Throwable) {
                        llSignUp.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                })
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }

    private fun initView() {
        llSignUp = findViewById(R.id.llSignUp)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etTel = findViewById(R.id.etTel)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }
}