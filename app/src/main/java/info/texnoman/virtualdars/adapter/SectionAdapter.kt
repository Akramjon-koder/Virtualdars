package info.texnoman.virtualdars.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.model.Section

class SectionAdapter(private val context: Context?, private val sectionList: List<Section>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_section,
            parent,
            false
        )
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = sectionList[position]

        if (holder is SectionViewHolder) {
            holder.tvTitle.text = "●  " + section.title
            holder.tvDuration.text = section.duration

            holder.rvCourseSubSection.layoutManager = LinearLayoutManager(context)
            val adapter = SubSectionAdapter(context, section.lessons)
            holder.rvCourseSubSection.adapter = adapter
        }
    }

    override fun getItemCount(): Int {
        return sectionList.size
    }

    inner class SectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val rvCourseSubSection: RecyclerView = itemView.findViewById(R.id.rvCourseSubSection)
    }
}