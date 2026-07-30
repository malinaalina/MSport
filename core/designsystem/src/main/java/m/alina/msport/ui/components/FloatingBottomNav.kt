package m.alina.msport.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.draw.clip
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.NunitoSans
import m.alina.msport.ui.theme.Surface as BloomSurface
import m.alina.msport.ui.theme.TextMuted

private val NavLabelStyle = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W700, fontSize = 12.5.sp)

@Composable
fun FloatingBottomNav(items: List<BottomNavItem>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        color = BloomSurface,
        shape = RoundedCornerShape(100.dp),
        shadowElevation = 14.dp,
    ) {
        Row(
            modifier = Modifier.padding(7.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { item ->
                val background = if (item.selected) Accent else Color.Transparent
                val textColor = if (item.selected) Color.White else TextMuted
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .background(background)
                        .clickable { item.onClick() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = item.label, style = NavLabelStyle, color = textColor)
                }
            }
        }
    }
}
