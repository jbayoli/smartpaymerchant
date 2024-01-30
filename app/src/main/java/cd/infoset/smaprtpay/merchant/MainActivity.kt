package cd.infoset.smaprtpay.merchant

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        )

        super.onCreate(savedInstanceState)

        setContent {
            FlexPayTheme {
                Surface {
                    FlexPayApp()
                }
            }
        }
    }
}
