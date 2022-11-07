package info.texnoman.virtualdars.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.SignUpVerificationResponse
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SignUpVerificationActivity : AppCompatActivity() {
    lateinit var llVerify: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var etCode: EditText
    lateinit var button: Button
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var tel: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_verification)

        firstName = Objects.requireNonNull(intent.extras)!!.getString("firstName").toString()
        lastName = Objects.requireNonNull(intent.extras)!!.getString("lastName").toString()
        tel = Objects.requireNonNull(intent.extras)!!.getString("tel").toString()
        password = Objects.requireNonNull(intent.extras)!!.getString("password").toString()

        initView()

        button.setOnClickListener{
            val code = etCode.text.toString()
            checkData(code)
        }

    }

    private fun initView() {
        llVerify = findViewById(R.id.llVerify)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        etCode = findViewById(R.id.etCode)
        button = findViewById(R.id.button)
    }

    private fun checkData(code: String) {
        if (code.isEmpty()) {
            Toast.makeText(applicationContext, "Ismni kiriting", Toast.LENGTH_SHORT).show()
        } else {
            uploadData(code)
        }
    }

    private fun uploadData(code: String) {
        llVerify.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance.signUpVerification(code, tel).enqueue(object : Callback<SignUpVerificationResponse> {
            override fun onResponse(call: Call<SignUpVerificationResponse>, response: Response<SignUpVerificationResponse>) {
                if (response.isSuccessful) {
                    val verify = response.body()

                    if (verify!!.success) {
                        val token = verify.data.token

                        Paper.book().write("firstName", firstName)
                        Paper.book().write("lastName", lastName)
                        Paper.book().write("token", token)
                        Paper.book().write("tel", tel)
                        Paper.book().write("password", password)

                        Toast.makeText(applicationContext, verify.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, verify.message, Toast.LENGTH_SHORT).show()
                        llVerify.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                } else {
                    llVerify.isVisible = true
                    lottieAnimationView.isVisible = false
                }
            }

            override fun onFailure(call: Call<SignUpVerificationResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Xatolik", Toast.LENGTH_SHORT).show()
                llVerify.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }
}