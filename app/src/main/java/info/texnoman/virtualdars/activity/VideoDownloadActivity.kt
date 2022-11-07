package info.texnoman.virtualdars.activity
import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.gson.GsonBuilder
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.adapter.VideoDownloadAdapter
import info.texnoman.virtualdars.adapter.VideoPagerAdapter
import info.texnoman.virtualdars.model.VideoData
import info.texnoman.virtualdars.utils.Utils
import io.paperdb.Paper
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import top.xuqingquan.m3u8downloader.FileDownloader
import top.xuqingquan.m3u8downloader.entity.*
import java.io.File
import kotlin.concurrent.thread
//@RuntimePermissions
class VideoDownloadActivity : AppCompatActivity()/*, VideoDownloadAdapter.OnItemClickListener*/ {
  /*  private lateinit var adapter: VideoDownloadAdapter
    private val videoList = arrayListOf<VideoDownloadEntity>()
    private val tempList = arrayListOf<String>()
    private val gson = GsonBuilder().create()
    private lateinit var list: RecyclerView
    lateinit var videoView: VideoView
    private var mediaController: MediaController? = null*/
  companion object {
      private const val CACHE_SIZE = 50 * 1024 * 1024L
      private var cacheInstance: Cache? = null
      private const val TAG = "MainActivity"
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
            streamUrl = "https://edge.tikicdn.com/data/hls/902297/1/3/1478/manifest.m3u8"
        ),
        VideoData(
            id = i++,
            streamUrl = "https://edge.tikicdn.com/data/hls/901262/1/3/1478/manifest.m3u8"
        ),
        VideoData(
            id = i++,
            streamUrl = "https://edge.tikicdn.com/data/hls/901261/1/3/1478/manifest.m3u8"
        )
    )


    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_download)
       var viewPager=findViewById<ViewPager2>(R.id.viewPager)

        viewPager.apply {
            adapter = VideoPagerAdapter(this@VideoDownloadActivity).apply {
                showDetails = this@VideoDownloadActivity.videoDatas
            }
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        if (currentItem != pagerLastItem.value) {
                            pagerLastItem.value = currentItem
                        }
                        player.playWhenReady = true
                    } else {
                        player.playWhenReady = false
                    }
                }
            })
        }



        /*list = findViewById<RecyclerView>(R.id.list)
        videoView = findViewById(R.id.videoView)
        initListView()
        initListWithPermissionCheck()
        Paper.init(this)

        FileDownloader.downloadCallback.observe(this, Observer {

            onProgress(it)
        })
        findViewById<AppCompatImageButton>(R.id.add).setOnClickListener {
            //      newDownload()
            download()
        }
     //   initList()*/
    }
    /*
    private fun download() {
        val url = Utils.BASE_URL + LessonActivity.videoUrl.media_stream_src
        tempList.forEach {
            Log.e("templist", it.toString())
        }
        if (tempList.contains(url)) {
            Toast.makeText(this, "Oldin yuklangan", Toast.LENGTH_SHORT).show()
            //  dialog.dismiss()
            return
        }
        val name = if (url.contains("?")) {
            url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"))
        } else {
            url.substring(url.lastIndexOf("/") + 1)
        }
        val entity = VideoDownloadEntity(url, name)
        entity.toFile()
        videoList.add(0, entity)
        adapter.notifyItemInserted(0)
        FileDownloader.downloadVideo(entity)
    }

    private fun initListView() {
        adapter = VideoDownloadAdapter(videoList, this)
        list.adapter = adapter
    }
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun initList() {
            FileDownloader.getBaseDownloadPath().listFiles().forEach {
                val file = File(it, "video.config")

                Log.e("filelist", it.toString())
                if (file.exists()) {
                    val text = file.readText()
                    if (text.isNotEmpty()) {
                        Log.e("filepath",text.toString())
                        val data = gson.fromJson<VideoDownloadEntity>(text, VideoDownloadEntity::class.java)
                        if (data != null) {
                            if (data.status == DELETE) {
                                it.deleteRecursively()
                            } else if (!tempList.contains(data.originalUrl)) {
                                videoList.add(data)
                                tempList.add(data.originalUrl)

                            }
                        }
                    }
                }

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
            videoList.sort()
            videoList.filter { it.status == DOWNLOADING }.forEach {
                FileDownloader.downloadVideo(it)
            }
            videoList.filter { it.status == PREPARE }.forEach {
                FileDownloader.downloadVideo(it)
            }
            videoList.filter { it.status == NO_START }.forEach {
                FileDownloader.downloadVideo(it)
            }
        }
    }

    @OnPermissionDenied(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onDenied() {
        Toast.makeText(this, "Ruxsat bermadizda", Toast.LENGTH_SHORT).show()
    }

    private fun onProgress(entity: VideoDownloadEntity) {
        for ((index, item) in videoList.withIndex()) {
            if (item.originalUrl == entity.originalUrl) {
                videoList[index].status = entity.status
                videoList[index].currentSize = entity.currentSize
                videoList[index].currentSpeed = entity.currentSpeed
                videoList[index].currentProgress = entity.currentProgress
                videoList[index].fileSize = entity.fileSize
                videoList[index].tsSize = entity.tsSize
                videoList[index].downloadContext = entity.downloadContext
                videoList[index].downloadTask = entity.downloadTask
                videoList[index].startDownload = entity.startDownload
                adapter.notifyItemChanged(index, 0)
                break
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun newDownload() {
        val editText = EditText(this)
        editText.setHint("Urlni qo'ying")
        val downloadDialog = AlertDialog.Builder(this)
            .setView(editText)
            .setTitle("Yangi fayl")
            .setPositiveButton("OK") { dialog, _ ->
                if (editText.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Bo'sh bo'lmasin", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

            }
            .setNegativeButton("Chiqish") { dialog, _ ->
                dialog.dismiss()
            }.create()
        downloadDialog.show()
    }
    override fun OnClick(video: VideoDownloadEntity,position:Int) {
        Log.e("videourl", video.originalUrl.toString())
        var url =FileDownloader.getBaseDownloadPath().listFiles()[position].absolutePath+"/original.m3u8"
        Log.e("videourllocal",Uri.parse(url).toString())
        videoView.setVideoPath(Uri.parse(url).toString())
          videoView.requestFocus()
         // Log.e("baseurl", video.)
          mediaController = MediaController(this)
          mediaController?.setAnchorView(videoView)
          videoView.setMediaController(mediaController)
          videoView.seekTo(0)
          videoView.start()

    }*/

}