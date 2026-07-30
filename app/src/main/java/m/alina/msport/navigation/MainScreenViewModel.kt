package m.alina.msport.navigation

import m.alina.msport.mvi.BaseViewModel
import javax.inject.Inject

class MainScreenViewModel @Inject constructor() :
    BaseViewModel<MainScreenState, MainScreenAction, MainScreenEffect>(MainScreenState()) {

    override suspend fun handleAction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.SelectTab -> setState { copy(currentTab = action.tab) }
            MainScreenAction.WorkoutBooked -> sendEffect { MainScreenEffect.RefreshLists }
            MainScreenAction.WorkoutCancelled -> sendEffect { MainScreenEffect.RefreshLists }
            MainScreenAction.GoToMyClasses -> {
                setState { copy(currentTab = ScreenKey.Personal) }
                sendEffect { MainScreenEffect.NavigateToMyClassesUpcoming }
            }
            MainScreenAction.DismissConfirmation -> setState { copy(currentTab = ScreenKey.Schedule) }
        }
    }
}
