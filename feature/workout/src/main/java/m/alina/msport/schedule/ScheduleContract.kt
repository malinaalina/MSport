package m.alina.msport.schedule

import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState
import java.util.Date

data class ScheduleState(
    val currentMonth: String = "",
    val visibleDates: List<Date> = emptyList(),
    val selectedDate: Date = Date(),
    val activeTypeFilter: String? = null,
    val classes: List<WorkoutSession> = emptyList(),
    val isRefreshing: Boolean = false,
) : UiState

sealed interface ScheduleAction : UiAction {
    object NextWeek : ScheduleAction
    object PreviousWeek : ScheduleAction
    data class SelectDate(val date: Date) : ScheduleAction
    data class SetFilter(val type: String?) : ScheduleAction
    object Refresh : ScheduleAction
}

sealed interface ScheduleEffect : UiEffect
