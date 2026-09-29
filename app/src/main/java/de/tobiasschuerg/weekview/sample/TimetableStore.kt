package de.tobiasschuerg.weekview.sample

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class TimetableStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("class_board", Context.MODE_PRIVATE)
    private val today = LocalDate.now()
    private val academicYear = today.year - if (today.monthValue < 4) 1 else 0

    val terms =
        (
            (academicYear - 1..academicYear + 1).flatMap { year ->
                listOf("${year}年度 前期", "${year}年度 後期")
            } + preferences.all.keys.filter { it.startsWith("courses_") }.map { it.removePrefix("courses_") }
        ).distinct().sorted()
    private val sampleTerm = "${academicYear}年度 前期"

    fun selectedTerm(): String = preferences.getString("selected_term", sampleTerm) ?: sampleTerm

    fun selectTerm(term: String) {
        preferences.edit().putString("selected_term", term).apply()
    }

    fun load(term: String): List<Course> {
        val saved =
            preferences.getString("courses_$term", null)
                ?: return (if (term == sampleTerm) sampleCourses() else emptyList()).also { save(term, it) }
        val data = JSONArray(saved)
        return List(data.length()) { index ->
            val item = data.getJSONObject(index)
            Course(
                day = item.getInt("day"),
                period = item.getInt("period"),
                title = item.getString("title"),
                room = item.optString("room"),
                teacher = item.optString("teacher"),
            )
        }
    }

    fun save(
        term: String,
        courses: List<Course>,
    ) {
        val data = JSONArray()
        courses.forEach { course ->
            data.put(
                JSONObject()
                    .put("day", course.day)
                    .put("period", course.period)
                    .put("title", course.title)
                    .put("room", course.room)
                    .put("teacher", course.teacher),
            )
        }
        preferences.edit().putString("courses_$term", data.toString()).apply()
    }

    private fun sampleCourses() =
        listOf(
            Course(0, 0, "英語", "201A"),
            Course(0, 1, "英語", "201A"),
            Course(0, 2, "国際政治", "705"),
            Course(0, 3, "国際政治", "705"),
            Course(1, 0, "統計学", "420"),
            Course(1, 1, "統計学", "420"),
            Course(1, 2, "ソフトボール", "体育館B"),
            Course(1, 3, "ソフトボール", "体育館B"),
            Course(2, 0, "英語", "620"),
            Course(2, 1, "社会学", "308"),
            Course(2, 2, "社会学", "308"),
            Course(2, 4, "English", "1023"),
            Course(3, 0, "マクロ経済学", "808"),
            Course(3, 1, "マクロ経済学", "808"),
            Course(3, 3, "English", "1023"),
            Course(4, 0, "スピーキング", "ランゲージセンター"),
            Course(4, 1, "スペイン語", "多目的ルーム"),
            Course(4, 2, "スペイン語", "多目的ルーム"),
            Course(4, 3, "都市論", "506"),
            Course(4, 4, "マクロ経済学", "未設定"),
        )
}
