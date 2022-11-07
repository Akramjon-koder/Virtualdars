package info.texnoman.virtualdars.model

data class ActiveLesson(val id: Int, val title: String, val media_stream_src: String,
                        val isCompleted: Boolean, val mediaDuration: String, val type: String,
                        val startTime: Int, val hasStreamedVideo: Boolean, val nextLessonIsOpen: Boolean)