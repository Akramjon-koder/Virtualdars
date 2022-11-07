package top.xuqingquan.m3u8downloader.entity

import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadTask
import org.json.JSONObject
import top.xuqingquan.m3u8downloader.FileDownloader
import java.io.File
const val NO_START = 0
const val PREPARE = 1
const val DOWNLOADING = 2
const val PAUSE = 3
const val COMPLETE = 4
const val ERROR = 5
const val DELETE = -1

class VideoDownloadEntity(
    var originalUrl: String,
    var name: String = "",
    var subName: String = "",
    var redirectUrl: String = "",
    var fileSize: Long = 0,
    var currentSize: Long = 0,
    var currentProgress: Double = 0.0,
    var currentSpeed: String = "",
    var tsSize: Int = 0,
    var createTime: Long = System.currentTimeMillis()
) : Parcelable, Comparable<VideoDownloadEntity> {

    //状态
    var status: Int = NO_START
        set(value) {
            if (field != DELETE) {
                field = value
            }
            if (value == DELETE) {
                startDownload = null
                downloadContext?.stop()
                downloadTask?.cancel()
            }
        }
    var downloadContext: DownloadContext? = null
    var downloadTask: DownloadTask? = null
    var startDownload: (() -> Unit)? = null

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong()
    ) {
        this.status = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(originalUrl)
        parcel.writeString(name)
        parcel.writeString(subName)
        parcel.writeString(redirectUrl)
        parcel.writeLong(fileSize)
        parcel.writeLong(currentSize)
        parcel.writeDouble(currentProgress)
        parcel.writeString(currentSpeed)
        parcel.writeInt(tsSize)
        parcel.writeLong(createTime)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoDownloadEntity> {
        override fun createFromParcel(parcel: Parcel): VideoDownloadEntity {
            return VideoDownloadEntity(parcel)
        }

        override fun newArray(size: Int): Array<VideoDownloadEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        val json = JSONObject()
        json.put("originalUrl", originalUrl)
        json.put("name", name)
        json.put("subName", subName)
        json.put("redirectUrl", redirectUrl)
        json.put("fileSize", fileSize)
        json.put("currentSize", currentSize)
        json.put("currentProgress", currentProgress)
        json.put("currentSpeed", currentSpeed)
        json.put("tsSize", tsSize)
        json.put("createTime", createTime)
        json.put("status", status)
        return json.toString()
    }

    fun toFile() {
       val path = FileDownloader.getDownloadPath(originalUrl)
        Log.e("path",path.toString())
        //val path: File = Environment.getDownloadCacheDirectory()//getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val config = File(path, "video.config")
        if (!config.exists() && this.createTime == 0L) {
            this.createTime = System.currentTimeMillis()
        }
        config.writeText(toString())
    }

    override fun compareTo(other: VideoDownloadEntity) =
        (other.createTime - this.createTime).toInt()
}

fun parseJsonToVideoDownloadEntity(jsonString: String): VideoDownloadEntity? {
    if (jsonString.isEmpty()) {
        return null
    }

    return try {
        val json = JSONObject(jsonString)
        val entity = VideoDownloadEntity(
            json.getString("originalUrl"),
            json.getString("name"),
            json.getString("subName"),
            json.getString("redirectUrl"),
            json.getLong("fileSize"),
            json.getLong("currentSize"),
            json.getDouble("currentProgress"),
            json.getString("currentSpeed"),
            json.getInt("tsSize"),
            json.getLong("createTime")
        )
        entity.status = json.getInt("status")
        entity
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}