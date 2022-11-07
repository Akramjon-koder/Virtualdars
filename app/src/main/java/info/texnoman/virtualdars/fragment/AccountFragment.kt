package info.texnoman.virtualdars.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.ChangePasswordActivity
import info.texnoman.virtualdars.activity.EditProfileActivity
import info.texnoman.virtualdars.activity.LoginActivity
import info.texnoman.virtualdars.activity.SearchActivity
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.*
import io.paperdb.Paper
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    lateinit var ivSearch: ImageView
    lateinit var rlMyProfile: RelativeLayout
    lateinit var rlEditProfile: RelativeLayout
    lateinit var rlChangePassword: RelativeLayout
    lateinit var rlLogOut: RelativeLayout
    lateinit var rlSignPlaceholder: RelativeLayout
    lateinit var tvName: TextView
    lateinit var btnLogin: Button

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        initView(root)

        ivSearch.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        val token = Paper.book().read<String>("token")
        if (token == null) {
            rlSignPlaceholder.isVisible = true
        } else {
            checkToken()
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
            CustomIntent.customType(context, "fadein-to-fadeout")
        }

        rlLogOut.setOnClickListener {

            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setMessage("Siz profilingizdan chiqib ketmoqchimisiz")
            builder.setPositiveButton("Chiqish") { _, _ ->

                Paper.book().delete("firstName")
                Paper.book().delete("lastName")
                Paper.book().delete("token")
                Paper.book().delete("tel")
                Paper.book().delete("password")

                Toast.makeText(context, "Profildan chiqildi", Toast.LENGTH_SHORT).show()

                rlMyProfile.isVisible = false
                rlSignPlaceholder.isVisible = true
            }
            builder.setNegativeButton("Qaytish") { dialog, _ ->
                dialog.cancel()
            }

            val dialog = builder.create()
            dialog.show()
        }

        rlEditProfile.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        rlChangePassword.setOnClickListener {
            startActivity(Intent(context, ChangePasswordActivity::class.java))
        }

        return root
    }

    private fun initView(root: View) {
        ivSearch = root.findViewById(R.id.ivSearch)
        rlMyProfile = root.findViewById(R.id.rlMyProfile)
        rlEditProfile = root.findViewById(R.id.rlEditProfile)
        rlChangePassword = root.findViewById(R.id.rlChangePassword)
        rlLogOut = root.findViewById(R.id.rlLogOut)
        rlSignPlaceholder = root.findViewById(R.id.rlSignPlaceholder)
        tvName = root.findViewById(R.id.tvName)
        btnLogin = root.findViewById(R.id.btnLogin)
    }

    private fun checkToken() {
        RetrofitClient.instance_2.checkToken().enqueue(object: Callback<CheckToken> {
            override fun onResponse(call: Call<CheckToken>, response: Response<CheckToken>) {
                if (response.isSuccessful) {
                    val checkToken = response.body()!!

                    if (checkToken.success) {
                        val firstName = Paper.book().read<String>("firstName")
                        val lastName = Paper.book().read<String>("lastName")

                        rlMyProfile.isVisible = true
                        tvName.text = "$lastName $firstName"
                    } else {
                        Toast.makeText(context, checkToken.message, Toast.LENGTH_SHORT).show()
                        rlSignPlaceholder.isVisible = true
                    }

                } else {
                    Toast.makeText(context, "Xatolik", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckToken>, t: Throwable) {

            }
        })
    }
}