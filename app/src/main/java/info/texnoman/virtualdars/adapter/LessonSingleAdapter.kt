package info.texnoman.virtualdars.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.model.LessonSingle

class LessonSingleAdapter(private val listener: LessonSectionAdapter.OnItemClickListener,
                          private val context: Context?,
                          private val lessonSingleList: List<LessonSingle>,
                          private val activeId: Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_single_lesson,
            parent,
            false)

        return LessonSingleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lessonSingle = lessonSingleList[position]

        if (holder is LessonSingleViewHolder) {
            holder.tvTitle.text = lessonSingle.title
            holder.tvLessonNumber.text = (position + 1).toString()
            if (lessonSingle.isOpen) {
                holder.ivClose.isVisible = false
            }

            when (lessonSingle.type) {
                "video" -> {
//                    if (lessonSingle.id == activeId) {
//                        holder.ivVideoPlaying.isVisible = true
//                    } else {
//                        holder.ivVideo.isVisible = true
//                    }

                    holder.ivVideo.isVisible = true
                    holder.tvType.text = lessonSingle.mediaDuration
                }
                "quiz" -> {
                    holder.ivQuiz.isVisible = true
                    holder.tvType.text = "Quiz"
                }
                "task" -> {
                    holder.ivTask.isVisible = true
                    holder.tvType.text = "Task"
                }
            }

            holder.relativeLayout.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(lessonSingle)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return lessonSingleList.size
    }

    inner class LessonSingleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeLayout)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val ivVideo: ImageView = itemView.findViewById(R.id.ivVideo)
        val ivVideoPlaying: ImageView = itemView.findViewById(R.id.ivVideoPlaying)
        val ivQuiz: ImageView = itemView.findViewById(R.id.ivQuiz)
        val ivTask: ImageView = itemView.findViewById(R.id.ivTask)
        val ivClose: ImageView = itemView.findViewById(R.id.ivClose)
        val tvLessonNumber: TextView = itemView.findViewById(R.id.tvLessonNumber)
    }
}