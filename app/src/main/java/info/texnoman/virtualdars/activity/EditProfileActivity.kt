package info.texnoman.virtualdars.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.EditProfileResponse
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    lateinit var llContent: LinearLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    lateinit var ivBack: ImageView
    lateinit var btnChangeProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initView()

        ivBack.setOnClickListener {
            finish()
        }

        val firstName = Paper.book().read<String>("firstName")
        val lastName = Paper.book().read<String>("lastName")

        etFirstName.setText(firstName)
        etLastName.setText(lastName)

        btnChangeProfile.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()

            checkData(firstName, lastName)
        }
    }

    private fun checkData(firstName: String, lastName: String) {
        when {
            firstName.isEmpty() -> {
                Toast.makeText(applicationContext, "Ismingizni kiriting", Toast.LENGTH_SHORT).show()
            }
            lastName.isEmpty() -> {
                Toast.makeText(applicationContext, "Familyangizni kiriting", Toast.LENGTH_SHORT).show()
            }
            else -> {
                uploadData(firstName, lastName)
            }
        }
    }

    private fun uploadData(firstName: String, lastName: String) {
        llContent.isVisible = false
        lottieAnimationView.isVisible = true

        RetrofitClient.instance_2.editProfile(firstName, lastName).enqueue(object :
            Callback<EditProfileResponse> {
            override fun onResponse(call: Call<EditProfileResponse>, response: Response<EditProfileResponse>) {
                if (response.isSuccessful) {
                    val model = response.body()

                    if (model!!.success) {
                        Toast.makeText(applicationContext, model.message, Toast.LENGTH_SHORT).show()

                        Paper.book().write("firstName", firstName)
                        Paper.book().write("lastName", lastName)

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

            override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                llContent.isVisible = true
                lottieAnimationView.isVisible = false
            }
        })
    }

    private fun initView() {
        ivBack = findViewById(R.id.ivBack)
        btnChangeProfile = findViewById(R.id.btnChangeProfile)
        llContent = findViewById(R.id.llContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
    }
}