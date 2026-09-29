package de.tobiasschuerg.weekview.sample

data class Course(
    val day: Int,
    val period: Int,
    val title: String,
    val room: String = "",
    val teacher: String = "",
)
