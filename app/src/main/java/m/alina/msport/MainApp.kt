package m.alina.msport

import android.app.Application
import m.alina.msport.di.AppComponent
import m.alina.msport.di.DaggerAppComponent

class MainApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }
}
