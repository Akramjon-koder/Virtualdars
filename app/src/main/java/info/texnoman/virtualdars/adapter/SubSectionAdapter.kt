package info.texnoman.virtualdars.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.PlayVideoActivity
import info.texnoman.virtualdars.model.SubSection

class SubSectionAdapter(private val context: Context?, private val subSectionList: List<SubSection>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_sub_section,
            parent,
            false)

        return SubSectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subSection = subSectionList[position]

        if (holder is SubSectionViewHolder) {
            holder.tvTitle.text = subSection.title

            when (subSection.type) {
                "video" -> {
                    holder.ivVideo.isVisible = true

                    if (subSection.is_open == 1) {
                        holder.ivVideoOpen.isVisible = true
                    }
                }
                "quiz" -> {
                    holder.ivQuiz.isVisible = true
                }
                "task" -> {
                    holder.ivTask.isVisible = true
                }
            }

            holder.tvTitle.setOnClickListener {
                if (subSection.is_open == 1) {
                    if (subSection.openStreamSrc == "") {
                        Toast.makeText(context, "Video topilmadi", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(context, PlayVideoActivity::class.java)
                        intent.putExtra("youtube_video", false)
                        intent.putExtra("video_url", subSection.openStreamSrc)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context?.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return subSectionList.size
    }

    inner class SubSectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val ivVideo: ImageView = itemView.findViewById(R.id.ivVideo)
        val ivVideoOpen: ImageView = itemView.findViewById(R.id.ivVideoOpen)
        val ivQuiz: ImageView = itemView.findViewById(R.id.ivQuiz)
        val ivTask: ImageView = itemView.findViewById(R.id.ivTask)
    }
}