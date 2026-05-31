package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Success,
    background = BgDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextColorDark,
    onSurface = TextColorDark,
    surfaceVariant = Surface2Dark,
    outline = BorderDark,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Success,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    outline = Color(0xFFCBD5E1),
    error = Error
)

@Composable
fun MyApplicationTheme(
  appTheme: String = "Blue",
  darkMode: String = "System",
  activeFont: String = "Default",
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val isDark = when(darkMode) {
      "Light" -> false
      "Dark" -> true
      else -> isSystemInDarkTheme()
  }

  val primaryColor = when(appTheme) {
      "Green" -> Color(0xFF10b981)
      "Purple" -> Color(0xFF8b5cf6)
      "Orange" -> Color(0xFFf59e0b)
      "Red" -> Color(0xFFef4444)
      else -> Color(0xFF3b82f6)
  }

  val fontFamily = when(activeFont) {
      "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
      "Monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
      else -> androidx.compose.ui.text.font.FontFamily.SansSerif
  }

  val baseColorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      isDark -> DarkColorScheme
      else -> LightColorScheme
    }

  val colorScheme = baseColorScheme.copy(primary = primaryColor)
  
  val typography = androidx.compose.material3.Typography(
      headlineLarge = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
      headlineMedium = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
      titleLarge = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
      bodyLarge = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
      bodyMedium = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
      labelSmall = androidx.compose.ui.text.TextStyle(fontFamily = fontFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
  )

  MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}
