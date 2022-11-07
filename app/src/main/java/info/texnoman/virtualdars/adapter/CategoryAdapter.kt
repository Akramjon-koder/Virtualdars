package info.texnoman.virtualdars.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.CategoryCoursesActivity
import info.texnoman.virtualdars.model.Category
import info.texnoman.virtualdars.utils.Utils
import maes.tech.intentanim.CustomIntent

class CategoryAdapter(private val context: Context?, private val categoryList: List<Category>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_category,
            parent,
            false
        )

        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = categoryList[position]

        if (holder is CategoryViewHolder) {
            holder.tvName.text = category.title

            if (category.coursesCount.toInt() == 0) {
                holder.tvCount.text = "Hozircha kurslar yo'q"
            } else {
                holder.tvCount.text = category.coursesCount + " ta kurs mavjud"
            }

            Glide.with(context!!).load(Utils.BASE_URL + category.image).centerCrop().into(holder.ivImage)

            holder.cardView.setOnClickListener {
                val intent = Intent(context, CategoryCoursesActivity::class.java)
                intent.putExtra("id", category.id)
                intent.putExtra("name", category.title)
                context.startActivity(intent)
                CustomIntent.customType(context, "fadein-to-fadeout")
            }

            val incrementalPosition = position + 1
            when {
                incrementalPosition % 3 == 0 -> {
                    holder.rlItem.setBackgroundColor(Color.parseColor("#29D0A8"))
                }
                (incrementalPosition - 2) % 3 == 0 -> {
                    holder.rlItem.setBackgroundColor(Color.parseColor("#F65053"))
                }
                else -> {
                    holder.rlItem.setBackgroundColor(Color.parseColor("#594CF5"))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val rlItem: RelativeLayout = itemView.findViewById(R.id.rlItem)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
    }
}