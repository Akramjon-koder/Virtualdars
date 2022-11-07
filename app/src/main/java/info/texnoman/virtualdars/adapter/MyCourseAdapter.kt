package info.texnoman.virtualdars.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.LessonActivity
import info.texnoman.virtualdars.model.MyCourseData
import info.texnoman.virtualdars.utils.Utils
import maes.tech.intentanim.CustomIntent

class MyCourseAdapter(private val context: Context?, private val myCourseDataList: List<MyCourseData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_my_course,
            parent,
            false
        )
        return MyCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myCourseData = myCourseDataList[position]

        if (holder is MyCourseViewHolder) {
            holder.tvName.text = myCourseData.title
            holder.tvCourseCompletion.text = myCourseData.completedPercent.toString() + "% Tugallandi"

            Glide.with(context!!).load(Utils.BASE_URL + myCourseData.image).centerCrop().into(holder.ivImage)

            holder.ratingBar.rating = myCourseData.rating
            holder.progressBar.progress = myCourseData.completedPercent

            holder.cardView.setOnClickListener {
                val intent = Intent(context, LessonActivity::class.java)
                intent.putExtra("id", myCourseData.id)
                context.startActivity(intent)
                CustomIntent.customType(context, "fadein-to-fadeout")
            }
        }
    }

    override fun getItemCount(): Int {
        return myCourseDataList.size
    }

    class MyCourseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCourseCompletion: TextView = itemView.findViewById(R.id.tvCourseCompletion)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}