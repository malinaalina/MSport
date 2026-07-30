package m.alina.msport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import m.alina.msport.R
import m.alina.msport.feature.workout.R as scheduleR
import m.alina.msport.feature.profile.R as profileR

enum class ScreenKey {
    Personal,
    Schedule,
    Profile,
}

@Composable
fun ScreenKey.label(): String = when (this) {
    ScreenKey.Personal -> stringResource(R.string.nav_my_classes)
    ScreenKey.Schedule -> stringResource(scheduleR.string.schedule_title)
    ScreenKey.Profile -> stringResource(profileR.string.profile_title)
}
