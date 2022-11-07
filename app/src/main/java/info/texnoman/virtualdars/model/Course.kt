package info.texnoman.virtualdars.model

data class Course(val id: Int, val title: String, val price: Int, val formattedPrice: String, val author: String, val image: String, val is_free: Int, val rating: Float, val ratingsCount: Int)