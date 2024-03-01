package cd.infoset.smaprtpay.merchant.screens.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.contactlessPaymentScreen(
    onLaunch: () -> Unit,
    onNavBack: () -> Unit,
) {
    composable(Screen.TapToPay.route) {
        ContactlessPaymentScreen(
            onNavBack = onNavBack,
            onLaunch = onLaunch
        )
    }
}