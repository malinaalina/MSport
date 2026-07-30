package m.alina.msport.schedule.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.domain.usecase.workout.GetScheduleUseCase
import m.alina.msport.mvi.BaseViewModel
import m.alina.msport.schedule.ScheduleAction
import m.alina.msport.schedule.ScheduleEffect
import m.alina.msport.schedule.ScheduleState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ScheduleViewModel @Inject constructor(
    private val getSchedule: GetScheduleUseCase,
) : BaseViewModel<ScheduleState, ScheduleAction, ScheduleEffect>(ScheduleState()) {

    // Календарь, указывающий на начало текущей видимой недели (сегодня)
    private val calendar = Calendar.getInstance()

    private val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var allClassesForDay: List<WorkoutSession> = emptyList()

    init {
        setState { copy(selectedDate = calendar.time) }
        updateWeekState()
    }

    override suspend fun handleAction(action: ScheduleAction) {
        when (action) {
            ScheduleAction.NextWeek -> {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                updateWeekState()
            }
            ScheduleAction.PreviousWeek -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                updateWeekState()
            }
            is ScheduleAction.SelectDate -> selectDate(action.date)
            is ScheduleAction.SetFilter -> {
                setState { copy(activeTypeFilter = action.type) }
                applyFilter()
            }
            ScheduleAction.Refresh -> loadSchedule(currentState.selectedDate, forceRefresh = true)
        }
    }

    private fun selectDate(date: Date) {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        setState { copy(selectedDate = cal.time) }
        loadSchedule(cal.time)
    }

    private fun updateWeekState() {
        val weekDates = mutableListOf<Date>()
        val tempCal = calendar.clone() as Calendar

        repeat(7) {
            weekDates.add(tempCal.time)
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val month = monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        setState { copy(visibleDates = weekDates, currentMonth = month) }

        if (!isDateInList(currentState.selectedDate, weekDates)) {
            selectDate(weekDates.first())
        } else {
            loadSchedule(currentState.selectedDate)
        }
    }

    private fun isDateInList(date: Date, list: List<Date>): Boolean {
        val c2 = Calendar.getInstance().apply { time = date }
        return list.any { d ->
            val c1 = Calendar.getInstance().apply { time = d }
            c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
        }
    }

    private fun loadSchedule(date: Date, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val shouldShowLoader = forceRefresh || currentState.classes.isEmpty()
            if (shouldShowLoader) {
                setState { copy(isRefreshing = true) }
            }
            allClassesForDay = getSchedule(isoFormat.format(date), forceRefresh)
            applyFilter()
            setState { copy(isRefreshing = false) }
        }
    }

    private fun applyFilter() {
        val filter = currentState.activeTypeFilter
        val filtered = if (filter == null) {
            allClassesForDay
        } else {
            allClassesForDay.filter { it.title == filter }
        }
        setState { copy(classes = filtered) }
    }
}
