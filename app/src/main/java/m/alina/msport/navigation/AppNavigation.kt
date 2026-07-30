package m.alina.msport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import m.alina.msport.di.AppComponent
import m.alina.msport.di.rememberMainViewModels
import m.alina.msport.di.rememberPhoneLoginViewModel
import m.alina.msport.di.rememberPinSetupViewModel
import m.alina.msport.di.rememberSmsCodeViewModel
import m.alina.msport.phone.ui.PhoneLoginScreen
import m.alina.msport.pinsetup.ui.PinSetupScreen
import m.alina.msport.smscode.ui.SmsCodeScreen

private sealed interface AppRoute : NavKey {
    data object PhoneEntry : AppRoute
    data class SmsCode(val sessionId: String, val phone: String, val retryDelay: Int) : AppRoute
    data class PinSetup(val isReentry: Boolean, val phone: String) : AppRoute
    data object Main : AppRoute
}

@Composable
fun AppNavigation(appComponent: AppComponent) {
    val sessionManager = appComponent.sessionManager()
    val mainViewModels = rememberMainViewModels(appComponent)
    val backStack = remember {
        val startRoute: AppRoute = if (sessionManager.isAuthorized()) {
            AppRoute.PinSetup(isReentry = true, phone = "")
        } else {
            AppRoute.PhoneEntry
        }
        mutableStateListOf<AppRoute>(startRoute)
    }
    var authorizedPhone by remember { mutableStateOf("") }

    DisposableEffect(sessionManager) {
        val listener: () -> Unit = {
            backStack.clear()
            backStack.add(AppRoute.PinSetup(isReentry = true, phone = authorizedPhone))
        }
        sessionManager.addSessionExpiredListener(listener)
        onDispose { sessionManager.removeSessionExpiredListener(listener) }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.PhoneEntry> {
                val viewModel = rememberPhoneLoginViewModel(appComponent)
                PhoneLoginScreen(
                    viewModel = viewModel,
                    onSmsSent = { sessionId, retryDelay, phone ->
                        backStack.add(AppRoute.SmsCode(sessionId, phone, retryDelay))
                    },
                )
            }
            entry<AppRoute.SmsCode> { route ->
                val viewModel = rememberSmsCodeViewModel(appComponent, route.phone, route.sessionId, route.retryDelay)
                SmsCodeScreen(
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onVerified = { _, _ ->
                        backStack.add(AppRoute.PinSetup(isReentry = false, phone = route.phone))
                    },
                )
            }
            entry<AppRoute.PinSetup> { route ->
                val viewModel = rememberPinSetupViewModel(appComponent, route.isReentry)
                PinSetupScreen(
                    viewModel = viewModel,
                    onAuthorized = {
                        authorizedPhone = route.phone
                        backStack.clear()
                        backStack.add(AppRoute.Main)
                    },
                )
            }
            entry<AppRoute.Main> {
                MainScreen(
                    scheduleViewModel = mainViewModels.schedule,
                    myClassesViewModel = mainViewModels.myClasses,
                    classDetailViewModel = mainViewModels.classDetail,
                    mainScreenViewModel = mainViewModels.mainScreen,
                    phoneNumber = authorizedPhone,
                    onLogout = {
                        sessionManager.logout()
                        authorizedPhone = ""
                        backStack.clear()
                        backStack.add(AppRoute.PhoneEntry)
                    },
                )
            }
        },
    )
}
