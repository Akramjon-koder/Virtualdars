package info.texnoman.virtualdars.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.ChangePasswordResponse
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var llContent: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var ivBack: ImageView
    lateinit var btnChangePassword: Button
    lateinit var etCurrentPassword: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initView()

        ivBack.setOnClickListener {
            finish()
        }

        btnChangePassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            checkData(currentPassword, newPassword, confirmPassword)
        }
    }

    private fun initView() {
        llContent = findViewById(R.id.llContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        ivBack = findViewById(R.id.ivBack)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
    }

    private fun checkData(currentPassword: String, newPassword: String, confirmPassword: String) {
        when {
            currentPassword.isEmpty() -> {
                Toast.makeText(applicationContext, "Hozirgi parolingizni kiriting", Toast.LENGTH_SHORT).show()
            }
            newPassword.isEmpty() -> {
                Toast.makeText(applicationContext, "Yangi parolni", Toast.LENGTH_SHORT).show()
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(applicationContext, "Yangi parolni qayta kiriting", Toast.LENGTH_SHORT).show()
            }
            newPassword != confirmPassword -> {
                Toast.makeText(applicationContext, "Yangi parol bir xilligini tekshiring", Toast.LENGTH_SHORT).show()
            }
            currentPassword.length < 5 || newPassword.length < 5 || confirmPassword.length < 5 -> {
                Toast.makeText(applicationContext, "Kamida 5 ta belgidan iborat parol kiriting", Toast.LENGTH_SHORT).show()
            }
            else -> {
                uploadData(currentPassword, newPassword)
            }
        }
    }

    private fun uploadData(currentPassword: String, newPassword: String) {
        llContent.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance_2.changePassword(currentPassword, newPassword).enqueue(object :
            Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                if (response.isSuccessful) {
                    val model = response.body()

                    if (model!!.success) {
                        Toast.makeText(applicationContext, model.message, Toast.LENGTH_SHORT).show()

                        Paper.book().write("password", newPassword)

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, model.message, Toast.LENGTH_SHORT).show()
                        llContent.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    llContent.isVisible = true
                    lottieAnimationView.isVisible = false
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                llContent.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }
}