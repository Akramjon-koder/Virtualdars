package info.texnoman.virtualdars.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.FileDownloadAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.File
import info.texnoman.virtualdars.model.FileResponse
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FileDownloadActivity : AppCompatActivity(), FileDownloadAdapter.OnItemClickListener {
    lateinit var tvTitle: TextView
    lateinit var ivBack: ImageView
    lateinit var rvFiles: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_download)

        val lessonId = Objects.requireNonNull(intent.extras)!!.getInt("lessonId")
        initView()

        fileDownload(lessonId)

        ivBack.setOnClickListener {
            finish()
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
    }

    private fun fileDownload(lessonId: Int) {
        RetrofitClient.instance_2.fileDownload(lessonId).enqueue(object: Callback<FileResponse> {
            override fun onResponse(call: Call<FileResponse>, response: Response<FileResponse>) {
                if (response.isSuccessful) {
                    val fileDownload = response.body()!!

                    if (fileDownload.success) {
                        val files = fileDownload.data as MutableList<File>

                        val adapter = FileDownloadAdapter(this@FileDownloadActivity, applicationContext, files)
                        rvFiles.adapter = adapter
                    } else {
                        Toast.makeText(applicationContext, fileDownload.message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FileResponse>, t: Throwable) {

            }
        })
    }

    private fun initView() {
        ivBack = findViewById(R.id.ivBack)
        rvFiles = findViewById(R.id.rvFiles)
        rvFiles.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(file: File) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://virtualdars.uz${file.src}"))
        startActivity(intent)
    }
}