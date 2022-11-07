package info.texnoman.virtualdars.model

data class Section(val id: Int, val title: String, val duration: String, val lessons: List<SubSection>)