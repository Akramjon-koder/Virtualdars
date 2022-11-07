package info.texnoman.virtualdars.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.video.VideoDecoderGLSurfaceView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.VideoDownloadActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoFragment : Fragment() {
    private val mainActivity: VideoDownloadActivity
        get() = activity as VideoDownloadActivity

    private val player: SimpleExoPlayer
        get() = mainActivity.player
    private val uri by lazy { Uri.parse(mainActivity.videoDatas[position].streamUrl) }
    private val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)
    )
    private val mediaSource: MediaSource by lazy {
        val dataSourceFactory = mainActivity.cacheDataSourceFactory
        HlsMediaSource.Factory(dataSourceFactory)
            .setStreamKeys(cacheStreamKeys)
            .setAllowChunklessPreparation(true)
            .createMediaSource(uri)
    }

    private var position = -2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt(KEY_POSITION) ?: -2
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.pagerLastItem.observe(viewLifecycleOwner) {
            if (it == position) {
                cancelPreCache()
                Log.d(TAG, "Cancel preload at position: $position")
                player.stop(true)
                var renderView =view.findViewById<SimpleExoPlayerView>(R.id.renderView)
                renderView.player =player
                //  player.setVideoSurfaceView(renderView)
                //  Log.e("mediasourxe",mediaSource.toString())
                player.prepare(mediaSource)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            preCacheVideo()
        }
    }

    private val downloadConstructorHelper by lazy {
        DownloaderConstructorHelper(mainActivity.cache, mainActivity.upstreamDataSourceFactory, mainActivity.cacheDataSourceFactory, null,null)
    }

    private val downloader by lazy {
        HlsDownloader(uri, cacheStreamKeys, downloadConstructorHelper)
    }

    private fun cancelPreCache() {
        downloader.cancel()
    }

    private suspend fun preCacheVideo() = withContext(Dispatchers.IO) {
        runCatching {
            if (mainActivity.cache.isCached(uri.toString(), 0, PRE_CACHE_SIZE)) {
                Log.d(TAG, "video has been cached, return")
                return@runCatching
            }
            Log.d(TAG, "start pre-caching for position: $position")
            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= PRE_CACHE_SIZE) downloader.cancel()
                Log.d(TAG, "contentLength: $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded")
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d(TAG, "Cache fail for position: $position with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d(TAG, "Cache success for position: $position")
        }
        Unit
    }

    companion object {
        const val KEY_POSITION = "KEY_POSITION"
        private const val PRE_CACHE_SIZE = 5 * 1024 * 1024L
        private const val TAG = "VideoFragment"
    }
}