package info.texnoman.virtualdars.activity

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.utils.Utils
import java.util.*


class PlayVideoActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var textView2: TextView
    lateinit var videoView: VideoView
    lateinit var frameLayout: FrameLayout
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        val youtubeVideo: Boolean = Objects.requireNonNull(intent.extras)!!.getBoolean("youtube_video")
        val video_url = Objects.requireNonNull(intent.extras)!!.getString("video_url").toString()

        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)

        if (youtubeVideo) {
            var new_video_url = ""
            var index = 0

            for (i in video_url.indices) {
                if (video_url[i] == '=') {
                    index = i
                }
            }

            for (i in index + 1 until video_url.length) {
                new_video_url += video_url[i]
            }

            val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
            youTubePlayerView.isVisible = true

            lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {

                    val videoId = new_video_url
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
        } else {
            videoView = findViewById(R.id.videoView)
            frameLayout = findViewById(R.id.frameLayout)
            frameLayout.isVisible = true

            videoView.setVideoPath(Utils.BASE_URL + video_url)
            videoView.requestFocus()

            mediaController = MediaController(this)
            mediaController?.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.start()
        }
    }
}