package de.tobiasschuerg.weekview.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ComposeWeekViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val store = TimetableStore(this)
        setContent {
            MaterialTheme(
                colorScheme =
                    lightColorScheme(
                        primary = TimetableBlue,
                        onPrimary = Color.White,
                        surface = Color.White,
                        onSurface = Color(0xFF101018),
                        surfaceContainerHighest = Color(0xFFF1F1FF),
                    ),
            ) {
                TimetableScreen(store)
            }
        }
    }
}
