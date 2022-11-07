package info.texnoman.virtualdars.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.texnoman.virtualdars.R

class CourseAboutAdapter(private val context: Context?, private val aboutList: List<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_course_about,
            parent,
            false
        )

        return CourseAboutViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CourseAboutViewHolder) {
            holder.tvName.text = aboutList[position]
        }
    }

    override fun getItemCount(): Int {
        return aboutList.size
    }

    class CourseAboutViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}