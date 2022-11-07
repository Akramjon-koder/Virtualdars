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
import info.texnoman.virtualdars.model.WishlistData
import info.texnoman.virtualdars.utils.Utils
import maes.tech.intentanim.CustomIntent

class WishlistAdapter(private val listener: OnItemClickListener,
                      private val context: Context?,
                      private val wishlist: List<WishlistData>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemAddClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_wishlist,
            parent,
            false
        )
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val wish = wishlist[position]

        if (holder is ProductViewHolder) {
            holder.tvName.text = wish.title
            holder.tvCost.text = wish.formattedPrice
            holder.starRating.rating = wish.rating
            Glide.with(context!!).load(Utils.BASE_URL + wish.image).centerCrop().into(holder.ivImage)


            holder.cardView.setOnClickListener {
                val intent = Intent(context, CourseActivity::class.java)
                intent.putExtra("id", wish.id)
                context.startActivity(intent)
                CustomIntent.customType(context, "fadein-to-fadeout")
            }

            holder.ivWish.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemAddClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return wishlist.size
    }

    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val ivWish: ImageView = itemView.findViewById(R.id.ivWish)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        val starRating: RatingBar = itemView.findViewById(R.id.starRating)
    }
}