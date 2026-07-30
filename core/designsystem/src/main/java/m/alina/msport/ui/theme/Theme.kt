package m.alina.msport.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BloomColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentSoft,
    onPrimaryContainer = Accent,
    secondary = AccentSoft,
    onSecondary = Accent,
    tertiary = NavSurfaceTint,
    background = BackgroundGradientMid,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextMuted,
    outline = InputBorder,
    outlineVariant = Divider,
    error = Warning,
    errorContainer = WarningSoft,
)

@Composable
fun MSportTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BloomColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
