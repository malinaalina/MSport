package m.alina.msport.navigation

import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

data class MainScreenState(
    val currentTab: ScreenKey = ScreenKey.Schedule,
) : UiState

sealed interface MainScreenAction : UiAction {
    data class SelectTab(val tab: ScreenKey) : MainScreenAction
    object WorkoutBooked : MainScreenAction
    object WorkoutCancelled : MainScreenAction
    object GoToMyClasses : MainScreenAction
    object DismissConfirmation : MainScreenAction
}

sealed interface MainScreenEffect : UiEffect {
    object RefreshLists : MainScreenEffect
    object NavigateToMyClassesUpcoming : MainScreenEffect
}
