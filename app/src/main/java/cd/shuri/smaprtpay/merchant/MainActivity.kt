package cd.shuri.smaprtpay.merchant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cd.shuri.smaprtpay.merchant.databinding.ActivityMainBinding
import cd.shuri.smaprtpay.merchant.screens.splashscreen.SplashScreenFragmentDirections
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.appBar)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.singInFragment, R.id.homeFragment))
        binding.appBar.setupWithNavController(navController, appBarConfiguration)

        navigateToValidationTransactionIfNeeded(intent)
    }

    private fun setHomeDestinationForNotification() {
        val navGraph = navController.graph
        if (navController.currentDestination?.id != R.id.homeFragment) {
            navGraph.startDestination = R.id.homeFragment
            navController.graph = navGraph
        }
    }

    private fun navigateToValidationTransactionIfNeeded(intent: Intent?) {
        if (intent?.action == "cd.infoset.smartpay.merchant.ACTION_SHOW_VALIDATE_FRAGMENT") {
            navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToTransactionValidation())
        }
    }

    override fun onRestart() {
        super.onRestart()
        Timber.d("onRestart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }
}
