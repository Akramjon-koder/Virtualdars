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
import info.texnoman.virtualdars.model.ResetVerificationResponse
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ResetVerificationActivity : AppCompatActivity() {
    lateinit var llVerification: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var etCode: EditText
    lateinit var button: Button
    lateinit var password: String
    lateinit var tel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_verification)

        password = Objects.requireNonNull(intent.extras)!!.getString("password").toString()
        tel = Objects.requireNonNull(intent.extras)!!.getString("tel").toString()

        initView()

        button.setOnClickListener{
            val code = etCode.text.toString()
            checkData(code)
        }
    }

    private fun initView() {
        llVerification = findViewById(R.id.llVerification)
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
        llVerification.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance.resetVerification(code, tel, password).enqueue(object : Callback<ResetVerificationResponse> {
            override fun onResponse(call: Call<ResetVerificationResponse>, response: Response<ResetVerificationResponse>) {
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
                        llVerification.isVisible = true
                        lottieAnimationView.isVisible = false
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    llVerification.isVisible = true
                    lottieAnimationView.isVisible = false
                }
            }

            override fun onFailure(call: Call<ResetVerificationResponse>, t: Throwable) {
                llVerification.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }
}