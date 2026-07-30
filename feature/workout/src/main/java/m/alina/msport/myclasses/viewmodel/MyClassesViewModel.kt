package m.alina.msport.myclasses.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import m.alina.msport.domain.usecase.workout.CancelBookingUseCase
import m.alina.msport.domain.usecase.workout.GetPastWorkoutsUseCase
import m.alina.msport.domain.usecase.workout.GetPersonalWorkoutsUseCase
import m.alina.msport.mvi.BaseViewModel
import m.alina.msport.myclasses.MyClassesAction
import m.alina.msport.myclasses.MyClassesEffect
import m.alina.msport.myclasses.MyClassesState
import m.alina.msport.myclasses.MyClassesSubTab
import javax.inject.Inject

class MyClassesViewModel @Inject constructor(
    private val getPersonalWorkouts: GetPersonalWorkoutsUseCase,
    private val getPastWorkouts: GetPastWorkoutsUseCase,
    private val cancelBooking: CancelBookingUseCase,
) : BaseViewModel<MyClassesState, MyClassesAction, MyClassesEffect>(MyClassesState()) {

    init {
        viewModelScope.launch { refreshAll() }
    }

    override suspend fun handleAction(action: MyClassesAction) {
        when (action) {
            MyClassesAction.SelectUpcoming -> setState { copy(subTab = MyClassesSubTab.UPCOMING) }
            MyClassesAction.SelectPast -> setState { copy(subTab = MyClassesSubTab.PAST) }
            MyClassesAction.RefreshAll -> refreshAll()
            is MyClassesAction.Cancel -> cancel(action.workoutId)
        }
    }

    private suspend fun refreshAll() {
        setState { copy(isRefreshing = true) }
        refreshUpcoming()
        val past = getPastWorkouts(forceRefresh = true)
        setState { copy(past = past, isRefreshing = false) }
    }

    private suspend fun refreshUpcoming() {
        val upcoming = getPersonalWorkouts(forceRefresh = true)
        setState { copy(upcoming = upcoming) }
    }

    private suspend fun cancel(workoutId: String) {
        cancelBooking(workoutId)
        refreshUpcoming()
    }
}
