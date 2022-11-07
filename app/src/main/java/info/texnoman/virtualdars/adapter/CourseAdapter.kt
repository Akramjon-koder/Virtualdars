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

class CourseAdapter(private val context: Context?, private val courses: List<Course>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_course,
            parent,
            false
        )

        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val course = courses[position]

        if (holder is CourseViewHolder) {
            holder.tvName.text = course.title
            holder.tvAuthor.text = course.author
            holder.tvCost.text = course.formattedPrice
            holder.ratingBar.rating = course.rating
            holder.tvRatingValue.text = course.rating.toString()
            holder.tvRatingUsers.text = "(${course.ratingsCount})"
            Glide.with(context!!).load(Utils.BASE_URL + course.image).centerCrop().into(holder.ivImage)

            holder.cardView.setOnClickListener {
                val intent = Intent(context, CourseActivity::class.java)
                intent.putExtra("id", course.id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    class CourseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        val tvRatingValue: TextView = itemView.findViewById(R.id.tvRatingValue)
        val tvRatingUsers: TextView = itemView.findViewById(R.id.tvRatingUsers)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}