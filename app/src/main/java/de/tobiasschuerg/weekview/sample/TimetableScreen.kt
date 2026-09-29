package de.tobiasschuerg.weekview.sample

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TimetableBlue = Color(0xFF1008FF)
private val Ink = Color(0xFF111116)
private val Days = listOf("月", "火", "水", "木", "金", "土")
private val PeriodTimes = listOf("9:00", "10:45", "13:00", "14:40", "16:35")
private val Condensed = FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
private val Headline = FontFamily(Typeface.create("sans-serif-thin", Typeface.NORMAL))

@Composable
fun TimetableScreen(store: TimetableStore) {
    var term by rememberSaveable { mutableStateOf(store.selectedTerm()) }
    var courses by remember(term) { mutableStateOf(store.load(term)) }
    var editingSlot by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier.fillMaxSize().background(TimetableBlue).safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val viewportWidth = maxWidth
            val boardWidth = maxWidth.coerceAtLeast(360.dp).coerceAtMost(720.dp)
            val rowHeight = ((maxHeight - 298.dp) / 5).coerceIn(96.dp, 138.dp) * LocalDensity.current.fontScale.coerceAtLeast(1f)
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(Modifier.width(boardWidth).padding(horizontal = 22.dp)) {
                    Spacer(Modifier.height(42.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "TIMETABLE",
                            color = Color.White,
                            fontSize = 80.sp,
                            lineHeight = 88.sp,
                            fontFamily = Headline,
                            fontWeight = FontWeight.ExtraLight,
                            letterSpacing = (-2).sp,
                            style = TextStyle(textGeometricTransform = TextGeometricTransform(scaleX = 0.48f)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Spacer(Modifier.width(14.dp))
                        Box(Modifier.size(39.dp).border(1.2.dp, Color.White, CircleShape))
                    }
                    TermSelector(term, (store.terms + term).distinct().sorted()) { selected ->
                        store.selectTerm(selected)
                        term = selected
                        editingSlot = null
                    }
                    Spacer(Modifier.height(20.dp))
                }
                Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Box(Modifier.width(viewportWidth.coerceAtLeast(boardWidth)), contentAlignment = Alignment.Center) {
                        TimetableGrid(
                            courses = courses,
                            rowHeight = rowHeight,
                            modifier = Modifier.width(boardWidth).padding(horizontal = 6.dp),
                            onCellClick = { day, period -> editingSlot = day * 5 + period },
                        )
                    }
                }
                Text(
                    "マスをタップして授業を編集",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 14.dp, bottom = 20.dp),
                )
            }
        }
    }

    editingSlot?.let { slot ->
        val day = slot / 5
        val period = slot % 5
        val course = courses.firstOrNull { it.day == day && it.period == period }
        CourseEditor(
            term = term,
            day = day,
            period = period,
            course = course,
            onDismiss = { editingSlot = null },
            onSave = { updated ->
                val next = courses.filterNot { it.day == day && it.period == period } + listOfNotNull(updated)
                store.save(term, next)
                courses = next
                editingSlot = null
            },
        )
    }
}

@Composable
private fun TermSelector(
    term: String,
    terms: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier.height(48.dp).clickable(role = Role.Button) { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                term,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = Condensed,
                fontWeight = FontWeight.Light,
                style = TextStyle(textGeometricTransform = TextGeometricTransform(scaleX = 0.82f)),
            )
            Spacer(Modifier.width(14.dp))
            Canvas(Modifier.size(13.dp, 8.dp)) {
                val middle = Offset(size.width / 2, size.height)
                drawLine(Color.White, Offset.Zero, middle, strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(Color.White, middle, Offset(size.width, 0f), strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            terms.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontWeight = if (option == term) FontWeight.Bold else FontWeight.Normal) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TimetableGrid(
    courses: List<Course>,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    onCellClick: (Int, Int) -> Unit,
) {
    val line = Color.White.copy(alpha = 0.9f)
    Column(modifier.border(0.6.dp, line)) {
        Row(Modifier.fillMaxWidth().height(42.dp)) {
            Box(Modifier.width(42.dp).fillMaxHeight().border(0.3.dp, line))
            Days.forEach { day ->
                Box(
                    Modifier.weight(1f).fillMaxHeight().border(0.3.dp, line),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(day, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Light)
                }
            }
        }
        PeriodTimes.forEachIndexed { period, time ->
            Row(Modifier.fillMaxWidth().height(rowHeight)) {
                Column(
                    Modifier.width(42.dp).fillMaxHeight().border(0.3.dp, line),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("${period + 1}限", color = Color.White, fontFamily = Condensed, fontSize = 15.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(time, color = Color.White, fontFamily = Condensed, fontSize = 14.sp, fontWeight = FontWeight.Light)
                }
                Days.forEachIndexed { day, label ->
                    val course = courses.firstOrNull { it.day == day && it.period == period }
                    Box(
                        Modifier.weight(1f).fillMaxHeight().border(0.3.dp, line)
                            .padding(1.5.dp)
                            .background(if (course != null) Color(0xFFFFFEFC) else Color.Transparent)
                            .semantics {
                                contentDescription = "${label}曜日 ${period + 1}限 $time、" +
                                    (course?.let { "${it.title}、${it.room}、編集" } ?: "空き、授業を追加")
                            }
                            .clickable(role = Role.Button) { onCellClick(day, period) }
                            .padding(horizontal = 3.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (course != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    course.title,
                                    color = Ink,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    style =
                                        TextStyle(
                                            textGeometricTransform = TextGeometricTransform(scaleX = 0.82f),
                                            lineBreak = LineBreak.Heading,
                                            localeList = LocaleList("ja"),
                                        ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (course.room.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        course.room,
                                        color = Ink,
                                        fontFamily = Condensed,
                                        fontSize = if (course.room.length > 6) 10.sp else 12.sp,
                                        lineHeight = if (course.room.length > 6) 12.sp else 15.sp,
                                        style =
                                            TextStyle(
                                                textGeometricTransform = TextGeometricTransform(scaleX = 0.82f),
                                                lineBreak = LineBreak.Heading,
                                                localeList = LocaleList("ja"),
                                            ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseEditor(
    term: String,
    day: Int,
    period: Int,
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (Course?) -> Unit,
) {
    var title by rememberSaveable(term, day, period) { mutableStateOf(course?.title.orEmpty()) }
    var room by rememberSaveable(term, day, period) { mutableStateOf(course?.room.orEmpty()) }
    var teacher by rememberSaveable(term, day, period) { mutableStateOf(course?.teacher.orEmpty()) }
    val focus = LocalFocusManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${Days[day]}曜日 ${period + 1}限") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("$term · ${PeriodTimes[period]}〜", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(80) },
                    label = { Text("授業名（必須）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it.take(80) },
                    label = { Text("教室") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it.take(80) },
                    label = { Text("担当教員") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                )
                if (course != null) {
                    TextButton(onClick = { onSave(null) }) {
                        Text("この授業を削除", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = { onSave(Course(day, period, title.trim(), room.trim(), teacher.trim())) },
            ) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}
