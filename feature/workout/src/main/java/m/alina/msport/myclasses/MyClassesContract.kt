package m.alina.msport.myclasses

import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

enum class MyClassesSubTab { UPCOMING, PAST }

data class MyClassesState(
    val subTab: MyClassesSubTab = MyClassesSubTab.UPCOMING,
    val upcoming: List<WorkoutSession> = emptyList(),
    val past: List<WorkoutSession> = emptyList(),
    val isRefreshing: Boolean = false,
) : UiState

sealed interface MyClassesAction : UiAction {
    object SelectUpcoming : MyClassesAction
    object SelectPast : MyClassesAction
    object RefreshAll : MyClassesAction
    data class Cancel(val workoutId: String) : MyClassesAction
}

sealed interface MyClassesEffect : UiEffect
