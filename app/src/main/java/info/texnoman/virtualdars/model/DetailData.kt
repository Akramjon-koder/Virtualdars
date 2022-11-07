package info.texnoman.virtualdars.model

import android.os.Parcel
import android.os.Parcelable

data class DetailData(val id: Int, val title: String, val slug: String, val is_free: Int,
                      val price: Int, val formattedPrice: String, val hasDiscount: Boolean,
                      val old_price: Int, val formattedOldPrice: String, val youtube: Boolean,
                      val rating: Float, val ratingsCount: Int, val video_link: String,
                      val image: String, val author: String, val students: Int,
                      val enrollmentDuration: String, val videoDuration: String,
                      val lessonsCount: Int, val includes: List<String>, val requirements: List<String>,
                      val benefits: List<String>, val sections: List<Section>,
                      val isWished: Boolean, val isEnrolled: Boolean)
