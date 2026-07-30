package m.alina.msport.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.ui.theme.BloomBadge
import m.alina.msport.ui.theme.BloomBodySmall
import m.alina.msport.ui.theme.BloomCardTitle
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.Surface as BloomSurfaceColor
import m.alina.msport.ui.theme.SurfaceAlt
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary

@Composable
fun ClassCard(
    workout: WorkoutSession,
    onClick: (() -> Unit)? = null,
    muted: Boolean = false,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val badge = classBadgeFor(workout)
    val textColor = if (muted) TextMuted else TextPrimary

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (muted) SurfaceAlt else BloomSurfaceColor,
        shape = RoundedCornerShape(BloomRadius),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.widthIn(min = 64.dp)) {
                    Text(text = workout.date, style = BloomBodySmall, color = TextMuted)
                    Text(text = workout.time, style = BloomCardTitle, color = textColor)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        text = workout.title,
                        style = BloomCardTitle,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${workout.room} · ${workout.instructor}",
                        style = BloomBodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Surface(color = badge.background, shape = RoundedCornerShape(100.dp)) {
                    Text(
                        text = badge.label,
                        style = BloomBadge,
                        color = badge.foreground,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
            trailing?.invoke(this)
        }
    }
}
