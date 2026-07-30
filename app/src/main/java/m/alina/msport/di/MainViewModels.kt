package m.alina.msport.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import m.alina.msport.classdetail.viewmodel.ClassDetailViewModel
import m.alina.msport.myclasses.viewmodel.MyClassesViewModel
import m.alina.msport.navigation.MainScreenViewModel
import m.alina.msport.phone.viewmodel.PhoneLoginViewModel
import m.alina.msport.pinsetup.viewmodel.PinSetupViewModel
import m.alina.msport.schedule.viewmodel.ScheduleViewModel
import m.alina.msport.smscode.viewmodel.SmsCodeViewModel

class MainViewModels(
    val schedule: ScheduleViewModel,
    val myClasses: MyClassesViewModel,
    val classDetail: ClassDetailViewModel,
    val mainScreen: MainScreenViewModel,
)

@Composable
fun rememberMainViewModels(appComponent: AppComponent): MainViewModels {
    val schedule = remember { appComponent.scheduleViewModel() }
    val myClasses = remember { appComponent.myClassesViewModel() }
    val classDetail = remember { appComponent.classDetailViewModel() }
    val mainScreen = remember { appComponent.mainScreenViewModel() }
    return remember(schedule, myClasses, classDetail, mainScreen) {
        MainViewModels(schedule, myClasses, classDetail, mainScreen)
    }
}

@Composable
fun rememberPhoneLoginViewModel(appComponent: AppComponent): PhoneLoginViewModel =
    remember { appComponent.phoneLoginViewModel() }

@Composable
fun rememberSmsCodeViewModel(
    appComponent: AppComponent,
    phone: String,
    sessionId: String,
    retryDelay: Int,
): SmsCodeViewModel = remember(sessionId) {
    appComponent.smsCodeViewModelFactory().create(phone, sessionId, retryDelay)
}

@Composable
fun rememberPinSetupViewModel(appComponent: AppComponent, isReentry: Boolean): PinSetupViewModel =
    remember(isReentry) { appComponent.pinSetupViewModelFactory().create(isReentry) }
