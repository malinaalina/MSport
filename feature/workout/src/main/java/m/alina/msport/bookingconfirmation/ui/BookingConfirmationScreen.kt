package m.alina.msport.bookingconfirmation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import m.alina.msport.feature.workout.R
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomBodySmall
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomCardTitle
import m.alina.msport.ui.theme.BloomDetailTitle
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.Success
import m.alina.msport.ui.theme.SuccessSoft
import m.alina.msport.ui.theme.Surface as BloomSurfaceColor
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary

@Composable
fun BookingConfirmationScreen(
    workout: WorkoutSession,
    bookingStatus: BookingStatus,
    onGoToMyClasses: () -> Unit,
    onDone: () -> Unit,
) {
    val isWaitlisted = bookingStatus == BookingStatus.WAITLISTED
    val title = stringResource(if (isWaitlisted) R.string.waitlisted_title else R.string.booked_title)
    val subtitle = stringResource(if (isWaitlisted) R.string.waitlisted_subtitle else R.string.booked_subtitle)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .padding(horizontal = 28.dp)
            .padding(top = 60.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(88.dp).background(SuccessSoft, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(Success, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.booking_success_checkmark),
                    color = Color.White,
                    style = BloomDetailTitle.copy(fontSize = 24.sp),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = title, style = BloomDetailTitle, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = BloomBody,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Spacer(modifier = Modifier.height(28.dp))
        Surface(
            color = BloomSurfaceColor,
            shape = RoundedCornerShape(BloomRadius),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Text(text = workout.title, style = BloomCardTitle, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${workout.date} · ${workout.time} · ${workout.room}",
                    style = BloomBodySmall,
                    color = TextMuted,
                )
                Text(text = workout.instructor, style = BloomBodySmall, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGoToMyClasses,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BloomRadius),
            contentPadding = PaddingValues(vertical = 17.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
        ) {
            Text(text = stringResource(R.string.my_classes_title), style = BloomButton)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onDone) {
            Text(text = stringResource(R.string.done_action), style = BloomBody, color = TextMuted)
        }
    }
}
