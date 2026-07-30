package m.alina.msport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import m.alina.msport.navigation.AppNavigation
import m.alina.msport.ui.theme.MSportTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (application as MainApp).appComponent
        enableEdgeToEdge()
        setContent {
            MSportTheme {
                AppNavigation(appComponent)
            }
        }
    }
}
