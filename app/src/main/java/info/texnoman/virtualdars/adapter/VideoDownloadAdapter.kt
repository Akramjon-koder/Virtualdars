package info.texnoman.virtualdars.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.utils.getSizeUnit
import top.xuqingquan.m3u8downloader.FileDownloader
import top.xuqingquan.m3u8downloader.entity.*
import java.text.DecimalFormat
class VideoDownloadAdapter(private val list: MutableList<VideoDownloadEntity>,var click:OnItemClickListener) :
    RecyclerView.Adapter<VideoDownloadAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_download_list, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
         holder.itemView.setOnClickListener {
              click.OnClick(list[position],position)
            }
            holder.updateProgress(list[position])

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.title)
        private val currentSize = view.findViewById<TextView>(R.id.current_size)
        private val speed = view.findViewById<TextView>(R.id.speed)
        private val url = view.findViewById<TextView>(R.id.url)
        private val download = view.findViewById<TextView>(R.id.download)
      //  val layout1 =view.findViewById<ConstraintLayout>(R.id.conslayout)

        @SuppressLint("SetTextI18n")
        fun setData(data: VideoDownloadEntity?) {
            if (data == null) {
                return
            }
            val context = view.context
            url.text = data.originalUrl
            val name = if (data.name.isNotEmpty()) {
                if (data.subName.isNotEmpty()) {
                    "${data.name}(${data.subName})"
                } else {
                    data.name
                }
            } else {
                if (data.subName.isNotEmpty()) {
                    "${context.getString(R.string.unknow_movie)}(${data.subName})"
                } else {
                    context.getString(R.string.unknow_movie)
                }
            }
            title.text = name
            updateProgress(data)
        }
        @SuppressLint("SetTextI18n")
        fun updateProgress(data: VideoDownloadEntity) {

            if (data.originalUrl.endsWith(".m3u8") || data.status == COMPLETE) {
                currentSize.text =
                    getSizeUnit(data.currentSize.toDouble())
            } else {
                currentSize.text =
                    "${getSizeUnit(data.currentSize.toDouble())}/${getSizeUnit(
                        data.fileSize.toDouble()
                    )}"
            }
            speed.text =
                "${DecimalFormat("#.##%").format(data.currentProgress)}|${data.currentSpeed}"
            val context = view.context
            //??????????????????
            when (data.status) {
                NO_START -> {
                    download.setTextColor(ContextCompat.getColor(context, R.color.blue))
                    download.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_download_prepare)
                    download.setText("Yuklashni bosing")
                    download.isVisible = true
                    speed.isVisible = false
                    currentSize.isVisible = false
                    currentSize.setText(R.string.wait_download)
                    download.setOnClickListener {
                        if (data.startDownload != null) {
                            data.startDownload!!.invoke()
                        } else {
                            FileDownloader.downloadVideo(data)
                        }
                    }
                }
                DOWNLOADING -> {
                    currentSize.isVisible = true
                    speed.isVisible = true
                    speed.setTextColor(ContextCompat.getColor(speed.context, R.color.blue))
                    download.isVisible = true
                    download.setText(R.string.pause)
                    download.setOnClickListener {
                        data.downloadContext?.stop()
                        data.downloadTask?.cancel()
                    }
                    download.setTextColor(ContextCompat.getColor(context, R.color.white))
                    download.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_blue_btn)
                }
                PAUSE -> {
                    currentSize.isVisible = true
                    download.setTextColor(ContextCompat.getColor(context, R.color.white))
                    download.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_blue_btn)
                    download.isVisible = true
                    download.setText(R.string.go_on)
                    download.setOnClickListener {
                        if (data.startDownload != null) {
                            data.startDownload!!.invoke()
                        } else {
                            FileDownloader.downloadVideo(data)
                        }
                    }
                    speed.isVisible = true
                    speed.setText(R.string.already_paused)
                    speed.setTextColor(ContextCompat.getColor(speed.context, R.color.red))
                }
                COMPLETE -> {
                    currentSize.isVisible = true
                    download.isVisible = false
                    speed.isVisible = false
                }
                PREPARE -> {
                    currentSize.isVisible = true
                    download.setText(R.string.prepareing)
                    currentSize.setText(R.string.wait_download)
                    download.isVisible = true
                    download.setOnClickListener {
                        if (data.startDownload != null) {
                            data.startDownload!!.invoke()
                        } else {
                            FileDownloader.downloadVideo(data)
                        }
                    }
                    download.setTextColor(ContextCompat.getColor(context, R.color.blue))
                    download.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_download_prepare)
                    speed.isVisible = false
                }
                ERROR -> {
                    currentSize.isVisible = false
                    speed.isVisible = false
                    download.isVisible = true
                    download.setText(R.string.retry)
                    download.setOnClickListener {
                        if (data.startDownload != null) {
                            data.startDownload!!.invoke()
                        } else {
                            FileDownloader.downloadVideo(data)
                        }
                    }
                    download.setTextColor(ContextCompat.getColor(context, R.color.white))
                    download.background =
                        ContextCompat.getDrawable(context, R.drawable.shape_blue_btn)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun OnClick(video:VideoDownloadEntity,position: Int)
    }

}