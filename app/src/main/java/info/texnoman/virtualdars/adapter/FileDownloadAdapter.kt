package info.texnoman.virtualdars.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.model.File
import info.texnoman.virtualdars.model.FileResponse
import retrofit2.Callback

class FileDownloadAdapter(private val listener: OnItemClickListener,
                          private val context: Context?,
                          private val files: List<File>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(file: File)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_file_downlod,
            parent,
            false
        )

        return FileDownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is FileDownloadViewHolder) {
            holder.tvName.text = files[position].title

            holder.relativeLayout.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(files[position])
                }
            }

//            holder.relativeLayout.setOnClickListener {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://virtualdars.uz + ${files[position].src}"))
//                context!!.startActivity(intent)
//            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class FileDownloadViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeLayout)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}