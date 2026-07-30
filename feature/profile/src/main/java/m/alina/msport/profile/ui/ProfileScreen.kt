package m.alina.msport.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.feature.profile.R
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.AccentSoft
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomBodySmall
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomCardTitle
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.BloomScreenTitle
import m.alina.msport.ui.theme.Divider
import m.alina.msport.ui.theme.Surface as BloomSurfaceColor
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import m.alina.msport.util.formatRussianPhone

@Composable
fun ProfileScreen(phone: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .padding(horizontal = 22.dp)
            .padding(bottom = 88.dp)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.profile_title),
            style = BloomScreenTitle,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(AccentSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.profile_avatar_initials),
                    style = BloomCardTitle.copy(fontSize = 22.sp),
                    color = Accent,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.profile_name),
                    style = BloomCardTitle.copy(fontSize = 18.sp),
                    color = TextPrimary,
                )
                Text(
                    text = phone.ifBlank { null }?.let { formatRussianPhone(it) } ?: stringResource(
                        R.string.profile_default_phone,
                    ),
                    style = BloomBodySmall,
                    color = TextMuted,
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))

        Surface(color = AccentSoft, shape = RoundedCornerShape(BloomRadius)) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Text(
                    text = stringResource(R.string.membership_label),
                    style = BloomBody.copy(
                        fontSize = 13.sp,
                        fontWeight = BloomCardTitle.fontWeight,
                        letterSpacing = 0.4.sp,
                    ),
                    color = Accent,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.membership_summary),
                    style = BloomBody.copy(
                        fontSize = 16.sp,
                        fontWeight = BloomCardTitle.fontWeight,
                    ),
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                            RoundedCornerShape(3.dp),
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.62f)
                            .fillMaxSize()
                            .background(Accent, RoundedCornerShape(3.dp)),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BloomRadius),
            contentPadding = PaddingValues(vertical = 15.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                TextPrimary.copy(alpha = 0.35f),
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        ) {
            Text(text = stringResource(R.string.logout_action), style = BloomButton)
        }
    }
}
