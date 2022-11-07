package info.texnoman.virtualdars.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.CourseActivity
import info.texnoman.virtualdars.model.Course
import info.texnoman.virtualdars.utils.Utils
import maes.tech.intentanim.CustomIntent

class TopCourseAdapter(private val context: Context?, private val courseList: List<Course>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_top_course,
            parent, false)

        return TopCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = courseList[position]

        if (holder is TopCourseViewHolder) {
            Glide.with(context!!).load(Utils.BASE_URL + product.image).centerCrop().into(holder.ivImage)
            holder.tvName.text = product.title
            holder.tvCost.text = product.formattedPrice
            holder.ratingBar.rating = product.rating

            holder.cardView.setOnClickListener {
                val intent = Intent(context, CourseActivity::class.java)
                intent.putExtra("id", product.id)
                context.startActivity(intent)
                CustomIntent.customType(context, "fadein-to-fadeout")
            }
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    inner class TopCourseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}