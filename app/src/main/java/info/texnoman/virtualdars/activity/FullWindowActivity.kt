package info.texnoman.virtualdars.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import info.texnoman.virtualdars.R
import java.util.*

class FullWindowActivity : AppCompatActivity() {
    lateinit var ivFullWindowExit: ImageView
    lateinit var videoView: VideoView
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_window)

        val url = Objects.requireNonNull(intent.extras)!!.getString("url")
        val currentPosition = Objects.requireNonNull(intent.extras)!!.getInt("currentPosition")

        ivFullWindowExit = findViewById(R.id.ivFullWindowExit)

        videoView = findViewById(R.id.videoView)
        videoView.setVideoPath(url)
        videoView.requestFocus()

        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.seekTo(currentPosition)
        videoView.start()

        ivFullWindowExit.setOnClickListener {
            finish()
        }
    }
}