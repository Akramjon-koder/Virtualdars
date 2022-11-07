package info.texnoman.virtualdars.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.LessonSectionAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.*
import info.texnoman.virtualdars.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
class LessonActivity : AppCompatActivity(), LessonSectionAdapter.OnItemClickListener {
    lateinit var rlContent: RelativeLayout
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var rlDownload: RelativeLayout
    lateinit var frameLayout: FrameLayout
    lateinit var ivFullWindow: ImageView
    lateinit var youTubePlayerView: YouTubePlayerView
    lateinit var ivTask: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var tvCompleteCourse: TextView
    lateinit var tvLesson: TextView
    lateinit var videoView: VideoView
    private var mediaController: MediaController? = null
    lateinit var rvCourseSection: RecyclerView
    lateinit var adapter: LessonSectionAdapter
    lateinit var nextUrl: String

    var nextLessonId: String = ""
    var showingVideo: Boolean = true

    private var lessonId: Int = 0

    private var index = 1
    private lateinit var saveYouTubePlayer: YouTubePlayer
    companion object {
        private const val CACHE_SIZE = 50 * 1024 * 1024L
        private var cacheInstance: Cache? = null
        private const val TAG = "MainActivity"

        lateinit var  videoUrl:ActiveLesson
    }
    val cache by lazy {
        return@lazy cacheInstance ?: run {
            val exoCacheDir = File("${cacheDir.absolutePath}/exo")
            Log.e("exoCacheDir",exoCacheDir.toString())
            Log.e("exoCacheDir","${cacheDir.absolutePath}/exo")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            SimpleCache(exoCacheDir, evictor, ExoDatabaseProvider(this)).also {
                cacheInstance = it
            }
        }
    }

    val upstreamDataSourceFactory by lazy { DefaultDataSourceFactory(this, "Android") }

    val cacheDataSourceFactory by lazy {
        CacheDataSourceFactory(
            cache,
            upstreamDataSourceFactory,
            FileDataSource.Factory(),
            CacheDataSinkFactory(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                    Log.d(
                        TAG, "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead")
                }

                override fun onCacheIgnored(reason: Int) {
                    Log.d(TAG, "onCacheIgnored. reason:$reason")
                }
            })
    }

    val player by lazy {
        SimpleExoPlayer.Builder(this)
            .build()
    }

    var pagerLastItem = MutableLiveData(0)
    private var i = -1
    val videoDatas = listOf(
        VideoData(
            id = i++,
            streamUrl = "https://onlinedars.uz/media/stream/47/1110/haamzygwvwfrfif/qulrqlrgabyrjwh/stream.m3u8"
        ),
        VideoData(
            id = i++,
            streamUrl = "https://onlinedars.uz/media/stream/30/724/lwgybliowcltyum/zsipmevqfetwlum/stream.m3u8"
        ),
        VideoData(
            id = i++,
            streamUrl = "https://edge.tikicdn.com/data/hls/901261/1/3/1478/manifest.m3u8"
        )
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        val id = Objects.requireNonNull(intent.extras)!!.getInt("id")
        initView()
        lessonCourse(id)
         findViewById<ImageView>(R.id.ivDownload).setOnClickListener{
               startActivity(Intent(this,VideoDownloadActivity::class.java))
         }
        ivFullWindow.setOnClickListener {
            val currentPosition = videoView.currentPosition

            val intent = Intent(applicationContext, FullWindowActivity::class.java)
            intent.putExtra("url", Utils.BASE_URL + nextUrl)
            intent.putExtra("currentPosition", currentPosition)
            startActivity(intent)
        }

        rlDownload.setOnClickListener {
            val intent = Intent(applicationContext, FileDownloadActivity::class.java)
            intent.putExtra("lessonId", lessonId)
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        player.release()
        showingVideo = false
    }

    private fun initView() {
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        rlContent = findViewById(R.id.rlContent)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        frameLayout = findViewById(R.id.frameLayout)
        rlDownload = findViewById(R.id.rlDownload)
        progressBar = findViewById(R.id.progressBar)
        tvCompleteCourse = findViewById(R.id.tvCompleteCourse)
        rvCourseSection = findViewById(R.id.rvLesson)
        rvCourseSection.layoutManager = LinearLayoutManager(applicationContext)
        ivTask = findViewById(R.id.ivTask)
        ivFullWindow = findViewById(R.id.ivFullWindow)
        tvLesson = findViewById(R.id.tvLesson)
        videoView = findViewById(R.id.videoView)
    }
    private fun lessonCourse(id: Int) {
        RetrofitClient.instance_2.getMyCourseLesson(id).enqueue(object : Callback<Lesson> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                if (response.isSuccessful) {
                    val lesson = response.body()

                    if (lesson!!.success) {
                        adapter = LessonSectionAdapter(this@LessonActivity, applicationContext, lesson.data.sections, lesson.data.activeLesson.id)
                        rvCourseSection.adapter = adapter

                        tvLesson.text = lesson.data.activeLesson.title

                        progressBar.progress = lesson.data.completedPercent
                        tvCompleteCourse.text = lesson.data.completedLessonsCount.toString() +
                                "/" + lesson.data.totalCoursesCount.toString() +
                                "  Tugatilgan darslar"

                        startLesson(lesson.data.activeLesson)

                        rlContent.isVisible = true
                        lottieAnimationView.isVisible = false

//                        if (lesson.data.activeLesson.type == "video" &&
//                            lesson.data.totalCoursesCount != lesson.data.completedLessonsCount) {
//                            autoSendRequest(lesson.data.activeLesson)
//                        }

                        if ((lesson.data.activeLesson.type == "video" || lesson.data.activeLesson.type == "youtube") &&
                            lesson.data.totalCoursesCount != lesson.data.completedLessonsCount &&
                            !lesson.data.activeLesson.nextLessonIsOpen) {
                            autoSendRequest(lesson.data.activeLesson)
                        }

                    } else {
                        Toast.makeText(applicationContext, lesson.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {

            }
        })
    }

    private fun startLesson(activeLesson: ActiveLesson) {
        nextUrl = activeLesson.media_stream_src

        videoUrl =activeLesson
     //  Log.e("url",Utils.BASE_URL+nextUrl.toString())
        when (activeLesson.type) {
            "video" -> {
                frameLayout.isVisible = true
                rlDownload.isVisible = false
                videoView.setVideoPath(Utils.BASE_URL + nextUrl)
                videoView.requestFocus()
             //   downloadImage(Utils.BASE_URL + nextUrl)
                Log.e("baseurl",Utils.BASE_URL + nextUrl)
                mediaController = MediaController(this)
                mediaController?.setAnchorView(videoView)
                videoView.setMediaController(mediaController)
                videoView.seekTo(activeLesson.startTime)
                videoView.start()
            }
            "task" -> {
                lessonId = activeLesson.id

                videoView.pause()
                rlDownload.isVisible = true
                frameLayout.isVisible = false
            }
            "youtube" -> {
                if (index > 1) {
                    nextVideoOnYotube()
                } else {
                    var new_video_url = ""
                    var index = 0

                    for (i in nextUrl.indices) {
                        if (nextUrl[i] == '=') {
                            index = i
                        }
                    }

                    for (i in index + 1 until nextUrl.length) {
                        new_video_url += nextUrl[i]
                    }

                    youTubePlayerView.isVisible = true

                    lifecycle.addObserver(youTubePlayerView)
                    youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            saveYouTubePlayer = youTubePlayer

                            val videoId = new_video_url
                            saveYouTubePlayer.loadVideo(videoId, 0f)
                        }
                    })
                }
            }
        }
    }

    private fun nextVideoOnYotube() {
        var new_video_url = ""
        var index = 0

        for (i in nextUrl.indices) {
            if (nextUrl[i] == '=') {
                index = i
            }
        }

        for (i in index + 1 until nextUrl.length) {
            new_video_url += nextUrl[i]
        }

        youTubePlayerView.isVisible = true

        lifecycle.addObserver(youTubePlayerView)
        val videoId = new_video_url
        saveYouTubePlayer.loadVideo(videoId, 0f)
    }

    private fun autoSendRequest(activeLesson: ActiveLesson) {
        Handler().postDelayed({
            if (showingVideo) {
                sendRequest(activeLesson)
            }
        }, 10000)
    }

    private fun sendRequest(activeLesson: ActiveLesson) {
        RetrofitClient.instance_2.sendRequest(activeLesson.id.toString()).enqueue(object :
                Callback<SendRequestResponse> {
            override fun onResponse(call: Call<SendRequestResponse>, response: Response<SendRequestResponse>) {
                if (response.isSuccessful) {
                    val sendRequestResponse = response.body()
                    if (sendRequestResponse!!.success) {
                        if (!sendRequestResponse.data.nextLessonIsOpened) {
                            autoSendRequest(activeLesson)
                        } else {
                            nextLessonId = sendRequestResponse.data.nextLessonId
                            showingVideo = false
                            Toast.makeText(applicationContext, "Keyingi video ochildi", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(applicationContext, sendRequestResponse.message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SendRequestResponse>, t: Throwable) {

            }
        })
    }




    private fun lessonTheme(lessonSingle: LessonSingle) {
        RetrofitClient.instance_2.getLessonTheme(lessonSingle.id)
                .enqueue(object : Callback<Lesson> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                        if (response.isSuccessful) {
                            val lesson = response.body()

                            if (lesson!!.success) {
                                adapter.notifyDataSetChanged()

                                tvLesson.text = lesson.data.activeLesson.title
                                progressBar.progress = lesson.data.completedPercent
                                tvCompleteCourse.text = lesson.data.completedLessonsCount.toString() + "/" +
                                        lesson.data.totalCoursesCount.toString() + " Tugatilgan darslar"

                                startLesson(lesson.data.activeLesson)

                                if ((lesson.data.activeLesson.type == "video" || lesson.data.activeLesson.type == "youtube")
                                        && lesson.data.totalCoursesCount != lesson.data.completedLessonsCount
                                        && !lesson.data.activeLesson.nextLessonIsOpen) {

                                            showingVideo = true
                                            autoSendRequest(lesson.data.activeLesson)
                                }

                            } else {
                                Toast.makeText(applicationContext, lesson.message, Toast.LENGTH_SHORT)
                                        .show()
                            }
                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }

                    override fun onFailure(call: Call<Lesson>, t: Throwable) {

                    }
                })
    }

    override fun onItemClick(lessonSingle: LessonSingle) {
        lessonTheme(lessonSingle)
        index++
    }






}