package de.tobiasschuerg.weekview.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TimetableSettingsDialog(
    config: TimetableConfig,
    onDismiss: () -> Unit,
    onSave: (TimetableConfig) -> Unit,
) {
    var dayCount by rememberSaveable { mutableIntStateOf(config.dayCount.coerceIn(5, 7)) }
    var periodCount by rememberSaveable { mutableIntStateOf(config.periodTimes.size.coerceIn(4, 8)) }
    val startingTimes =
        remember(config) {
            buildList {
                addAll(config.periodTimes.take(8))
                while (size < 8) {
                    add(if (isEmpty()) "09:00" else nextTime(last()))
                }
            }
        }
    var serializedTimes by rememberSaveable { mutableStateOf(startingTimes.joinToString("|")) }
    val times =
        serializedTimes.split('|').let { values ->
            (0 until 8).map { values.getOrNull(it) ?: "09:00" }
        }
    val draft = TimetableConfig(dayCount, times.take(periodCount))
    val error = draft.validationError()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("時間割の設定") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("表示する曜日", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5 to "月〜金", 6 to "月〜土", 7 to "月〜日").forEach { (count, label) ->
                        val selected = dayCount == count
                        TextButton(
                            onClick = { dayCount = count },
                            modifier =
                                Modifier.semantics {
                                    role = Role.RadioButton
                                    this.selected = selected
                                    contentDescription = "${label}曜日${if (selected) "、選択中" else ""}"
                                },
                        ) {
                            Text(
                                label,
                                color = if (selected) TimetableBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }

                Text("時限数", style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        enabled = periodCount > 4,
                        onClick = { periodCount-- },
                        modifier = Modifier.semantics { contentDescription = "時限数を減らす" },
                    ) { Text("−") }
                    Text("${periodCount}時限", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 8.dp))
                    TextButton(
                        enabled = periodCount < 8,
                        onClick = { if (periodCount < 8) periodCount++ },
                        modifier = Modifier.semantics { contentDescription = "時限数を増やす" },
                    ) { Text("＋") }
                }

                Text("各時限の開始時刻", style = MaterialTheme.typography.titleSmall)
                times.take(periodCount).forEachIndexed { index, time ->
                    OutlinedTextField(
                        value = time,
                        onValueChange = { value ->
                            val mutable = times.toMutableList()
                            mutable[index] = value.filter { it in '0'..'9' || it == ':' }.take(5)
                            serializedTimes = mutable.joinToString("|")
                        },
                        label = { Text("${index + 1}限") },
                        placeholder = { Text("09:00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    )
                }
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "表示する曜日や時限を減らしても、登録済みの授業は削除されません。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(enabled = error == null, onClick = { onSave(draft) }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}

private fun nextTime(value: String): String {
    val match = Regex("^(\\d{2}):(\\d{2})$").matchEntire(value) ?: return "09:00"
    val minutes = (match.groupValues[1].toInt() * 60 + match.groupValues[2].toInt() + 90).coerceAtMost(23 * 60 + 59)
    return String.format(java.util.Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60)
}
