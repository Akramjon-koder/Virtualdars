package info.texnoman.virtualdars.model

data class LessonData(val course_id: Int, val course_title: String, val totalCoursesCount: Int,
                      val completedLessonsCount: Int, val completedPercent: Int , val activeLesson: ActiveLesson,
                      val nextLesson: NextLesson, val sections: List<LessonSection>)
