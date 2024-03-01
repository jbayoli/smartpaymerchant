package cd.infoset.smaprtpay.merchant.screens.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen


fun NavGraphBuilder.requestPaymentNav(
    onNavBac: () -> Unit,
    onNavigateToTapToPhone: () -> Unit
) {
    composable(Screen.RequestPayment.route) {
        PaymentScreen(
            onNavBac = onNavBac,
            onNavigateToTapToPhone = onNavigateToTapToPhone
        )
    }
}