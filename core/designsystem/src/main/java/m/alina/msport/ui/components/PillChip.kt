package m.alina.msport.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBadge
import m.alina.msport.ui.theme.TextMuted

@Composable
fun PillChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background = if (isSelected) Accent else Color.Transparent
    val contentColor = if (isSelected) Color.White else TextMuted
    val border = if (isSelected) null else BorderStroke(1.5.dp, TextMuted.copy(alpha = 0.5f))

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(background)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(100.dp)) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(text = label, style = BloomBadge.copy(fontSize = 13.sp), color = contentColor)
    }
}
