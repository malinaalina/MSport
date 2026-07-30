package m.alina.msport.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import m.alina.msport.classdetail.viewmodel.ClassDetailViewModel
import m.alina.msport.database.di.DatabaseModule
import m.alina.msport.myclasses.viewmodel.MyClassesViewModel
import m.alina.msport.navigation.MainScreenViewModel
import m.alina.msport.phone.viewmodel.PhoneLoginViewModel
import m.alina.msport.pinsetup.viewmodel.PinSetupViewModel
import m.alina.msport.repository.SessionManager
import m.alina.msport.schedule.viewmodel.ScheduleViewModel
import m.alina.msport.smscode.viewmodel.SmsCodeViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class, DatabaseModule::class])
interface AppComponent {
    fun sessionManager(): SessionManager

    fun scheduleViewModel(): ScheduleViewModel
    fun myClassesViewModel(): MyClassesViewModel
    fun classDetailViewModel(): ClassDetailViewModel
    fun mainScreenViewModel(): MainScreenViewModel
    fun phoneLoginViewModel(): PhoneLoginViewModel
    fun smsCodeViewModelFactory(): SmsCodeViewModel.Factory
    fun pinSetupViewModelFactory(): PinSetupViewModel.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}
