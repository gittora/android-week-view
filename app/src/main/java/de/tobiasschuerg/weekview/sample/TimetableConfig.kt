package de.tobiasschuerg.weekview.sample

import java.time.LocalTime

data class TimetableConfig(
    val dayCount: Int = 6,
    val periodTimes: List<String> = listOf("09:00", "10:45", "13:00", "14:40", "16:35"),
) {
    fun validationError(): String? {
        if (dayCount !in 5..7) return "曜日数は5〜7日の範囲で設定してください。"
        if (periodTimes.size !in 4..MAX_PERIODS) return "時限数は4〜${MAX_PERIODS}時限の範囲で設定してください。"

        val parsedTimes =
            periodTimes.map { value ->
                if (!TIME_PATTERN.matches(value)) return "時刻はHH:mm形式で入力してください。"
                try {
                    LocalTime.parse(value)
                } catch (_: Exception) {
                    return "時刻はHH:mm形式で入力してください。"
                }
            }
        if (parsedTimes.zipWithNext().any { (previous, next) -> !next.isAfter(previous) }) {
            return "時刻は早い順に設定してください。"
        }
        return null
    }

    companion object {
        const val MAX_PERIODS = 8

        private val TIME_PATTERN = Regex("\\d{2}:\\d{2}")

        val Defaults = TimetableConfig()
    }
}
