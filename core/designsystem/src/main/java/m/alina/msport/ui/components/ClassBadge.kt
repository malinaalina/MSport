package m.alina.msport.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import m.alina.msport.designsystem.R
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.AccentSoft
import m.alina.msport.ui.theme.Divider
import m.alina.msport.ui.theme.Success
import m.alina.msport.ui.theme.SuccessSoft
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.Warning
import m.alina.msport.ui.theme.WarningSoft

data class ClassBadge(val label: String, val background: Color, val foreground: Color)

@Composable
fun classBadgeFor(workout: WorkoutSession): ClassBadge = when {
    workout.isPast -> ClassBadge(stringResource(R.string.badge_completed), Divider, TextMuted)
    workout.bookingStatus == BookingStatus.BOOKED -> ClassBadge(stringResource(R.string.badge_booked), SuccessSoft, Success)
    workout.bookingStatus == BookingStatus.WAITLISTED -> ClassBadge(stringResource(R.string.badge_waitlisted), WarningSoft, Warning)
    workout.taken >= workout.capacity -> ClassBadge(stringResource(R.string.badge_full), Divider, TextMuted)
    else -> ClassBadge(stringResource(R.string.badge_seats_available, workout.capacity - workout.taken), AccentSoft, Accent)
}
