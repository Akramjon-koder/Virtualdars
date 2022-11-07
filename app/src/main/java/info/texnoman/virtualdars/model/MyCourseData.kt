package info.texnoman.virtualdars.model

data class MyCourseData(val id: Int, val title: String, val image: String, val totalCoursesCount: Int,
                        val completedLessonsCount: Int, val completedPercent: Int, val rating: Float)
