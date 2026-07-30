package m.alina.msport.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import m.alina.msport.bookingconfirmation.ui.BookingConfirmationScreen
import m.alina.msport.classdetail.ui.ClassDetailScreen
import m.alina.msport.classdetail.viewmodel.ClassDetailViewModel
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.myclasses.MyClassesAction
import m.alina.msport.myclasses.ui.MyClassesScreen
import m.alina.msport.myclasses.viewmodel.MyClassesViewModel
import m.alina.msport.profile.ui.ProfileScreen
import m.alina.msport.schedule.ScheduleAction
import m.alina.msport.schedule.ui.ScheduleScreen
import m.alina.msport.schedule.viewmodel.ScheduleViewModel
import m.alina.msport.ui.components.BottomNavItem
import m.alina.msport.ui.components.FloatingBottomNav

private sealed interface DetailRoute : NavKey {
    data class ClassDetail(val workout: WorkoutSession) : DetailRoute
    data class BookingConfirmation(val workout: WorkoutSession, val status: BookingStatus) : DetailRoute
}

@Composable
fun MainScreen(
    scheduleViewModel: ScheduleViewModel,
    myClassesViewModel: MyClassesViewModel,
    classDetailViewModel: ClassDetailViewModel,
    mainScreenViewModel: MainScreenViewModel,
    phoneNumber: String,
    onLogout: () -> Unit,
) {
    val state by mainScreenViewModel.uiState.collectAsState()
    val detailBackStack = remember { mutableStateListOf<DetailRoute>() }

    LaunchedEffect(Unit) {
        mainScreenViewModel.effect.collect { effect ->
            when (effect) {
                MainScreenEffect.RefreshLists -> {
                    myClassesViewModel.onAction(MyClassesAction.RefreshAll)
                    scheduleViewModel.onAction(ScheduleAction.SelectDate(scheduleViewModel.currentState.selectedDate))
                }
                MainScreenEffect.NavigateToMyClassesUpcoming -> {
                    myClassesViewModel.onAction(MyClassesAction.SelectUpcoming)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (detailBackStack.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.currentTab) {
                    ScreenKey.Personal -> MyClassesScreen(
                        viewModel = myClassesViewModel,
                        onWorkoutClick = { detailBackStack.add(DetailRoute.ClassDetail(it)) },
                    )
                    ScreenKey.Schedule -> ScheduleScreen(
                        viewModel = scheduleViewModel,
                        onWorkoutClick = { detailBackStack.add(DetailRoute.ClassDetail(it)) },
                    )
                    ScreenKey.Profile -> ProfileScreen(
                        phone = phoneNumber,
                        onLogout = onLogout,
                    )
                }

                FloatingBottomNav(
                    items = ScreenKey.entries.map { screen ->
                        BottomNavItem(
                            label = screen.label(),
                            selected = state.currentTab == screen,
                            onClick = { mainScreenViewModel.onAction(MainScreenAction.SelectTab(screen)) },
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                )
            }
        } else {
            NavDisplay(
                backStack = detailBackStack,
                onBack = { detailBackStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<DetailRoute.ClassDetail> { route ->
                        ClassDetailScreen(
                            workout = route.workout,
                            viewModel = classDetailViewModel,
                            onBack = { detailBackStack.removeLastOrNull() },
                            onBooked = {
                                val status = classDetailViewModel.currentState.bookingStatus ?: BookingStatus.BOOKED
                                mainScreenViewModel.onAction(MainScreenAction.WorkoutBooked)
                                detailBackStack.removeLastOrNull()
                                detailBackStack.add(DetailRoute.BookingConfirmation(route.workout, status))
                            },
                            onCancelled = {
                                mainScreenViewModel.onAction(MainScreenAction.WorkoutCancelled)
                                detailBackStack.removeLastOrNull()
                            },
                        )
                    }
                    entry<DetailRoute.BookingConfirmation> { route ->
                        BookingConfirmationScreen(
                            workout = route.workout,
                            bookingStatus = route.status,
                            onGoToMyClasses = {
                                mainScreenViewModel.onAction(MainScreenAction.GoToMyClasses)
                                detailBackStack.clear()
                            },
                            onDone = {
                                mainScreenViewModel.onAction(MainScreenAction.DismissConfirmation)
                                detailBackStack.clear()
                            },
                        )
                    }
                },
            )
        }
    }
}
