package cd.infoset.smaprtpay.merchant

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme
import dagger.hilt.android.AndroidEntryPoint
import io.mpos.accessories.AccessoryFamily
import io.mpos.accessories.parameters.AccessoryParameters
import io.mpos.paybutton.MposUi
import io.mpos.paybutton.UiConfiguration
import io.mpos.provider.ProviderMode
import io.mpos.taptophone.EnrollResultIntent
import io.mpos.transactions.Currency
import io.mpos.transactions.parameters.TransactionParameters
import timber.log.Timber
import java.math.BigDecimal

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var mposUI: MposUi
//    private val transactionLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            val intent = it.data
//            if (intent?.getIntExtra("requestCode", 0) == MposUi.REQUEST_CODE_PAYMENT) {
//                when (it.resultCode) {
//                    MposUi.RESULT_CODE_APPROVED -> {
//                        val transactionIdentifier =
//                            it.data?.getStringExtra(MposUi.RESULT_EXTRA_TRANSACTION_IDENTIFIER)
//                        Toast.makeText(
//                            applicationContext,
//                            "Transaction approved\nIdentifier: $transactionIdentifier",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    MposUi.RESULT_CODE_FAILED -> {
//                        Toast.makeText(applicationContext, "Transaction failed", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        }

    private val enrollDeviceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (EnrollResultIntent.isEnrollmentSuccessful(it.resultCode, it.data)) {
            Timber.d("Enroll device done")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        )

        super.onCreate(savedInstanceState)

        mposUI = MposUi.create(
            providerMode = ProviderMode.TEST,
            merchantId = "12a8f03d-dc9f-48c3-8199-df61e2115f0a",
            merchantSecret = "Hg4AaKDudwux84O6wTtxiDSGrH4asd1G"
        )

        mposUI.configuration = UiConfiguration(
            terminalParameters = AccessoryParameters.Builder(
                AccessoryFamily.TAP_TO_PHONE
            ).integrated().build(),
            summaryFeatures = setOf(
                UiConfiguration.SummaryFeature.REFUND_TRANSACTION,
                UiConfiguration.SummaryFeature.SEND_RECEIPT_VIA_EMAIL,
                UiConfiguration.SummaryFeature.CAPTURE_TRANSACTION,
                UiConfiguration.SummaryFeature.RETRY_TRANSACTION
            ),
            defaultSummaryFeature = UiConfiguration.SummaryFeature.SEND_RECEIPT_VIA_EMAIL,
        )


        try {
            setContent {
                FlexPayTheme {
                    Surface {
                        FlexPayApp {
                            launchEnrollDevice()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }



    private fun launchEnrollDevice() {
//        val transactionParam = TransactionParameters.Builder()
//            .charge(BigDecimal("1.00"), Currency.USD)
//            .customIdentifier("0002839")
//            .build()
//        val intent = mposUI.createTransactionIntent(transactionParam)
        val intent = mposUI.tapToPhone.getEnrollDeviceIntent(this)
        try {
            //transactionLauncher.launch(intent)
            enrollDeviceLauncher.launch(intent)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
