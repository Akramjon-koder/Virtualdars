package info.texnoman.virtualdars.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.model.LessonSection
import info.texnoman.virtualdars.model.LessonSingle

class LessonSectionAdapter(private val listener: OnItemClickListener,
                           private val context: Context?,
                           private val lessonSectionList: List<LessonSection>,
                           private val activeId: Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(lessonSingle: LessonSingle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_lesson_section,
            parent,
            false)
        return LessonSectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = lessonSectionList[position]

        if (holder is LessonSectionViewHolder) {
            holder.tvTitle.text = section.title
            holder.tvSectionNumber.text = "${position + 1} - bo'lim"
            holder.tvSectionCount.text = "Darslar soni: ${lessonSectionList[position].lessons.size} ta"

            holder.rvSingleSection.layoutManager = LinearLayoutManager(context)
            val adapter = LessonSingleAdapter(listener, context, section.lessons, activeId)
            holder.rvSingleSection.adapter = adapter
        }
    }

    override fun getItemCount(): Int {
        return lessonSectionList.size
    }

    inner class LessonSectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSectionNumber: TextView = itemView.findViewById(R.id.tvSectionNumber)
        val tvSectionCount: TextView = itemView.findViewById(R.id.tvSectionCount)
        val rvSingleSection: RecyclerView = itemView.findViewById(R.id.rvSingleSection)
    }
}