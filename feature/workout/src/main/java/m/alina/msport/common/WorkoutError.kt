package m.alina.msport.common

import androidx.annotation.StringRes
import m.alina.msport.feature.workout.R

data class WorkoutError(
    @StringRes val messageRes: Int = R.string.error_unknown,
    val message: String? = null,
)
