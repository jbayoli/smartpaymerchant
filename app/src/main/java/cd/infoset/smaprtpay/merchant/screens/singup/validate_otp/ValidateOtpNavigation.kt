package cd.infoset.smaprtpay.merchant.screens.singup.validate_otp

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.validateOptScreen(
    onPopBackStack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    composable(Screen.ValidateOtp.route) {
        ValidateOtpScreen(
            onPopBackStack = onPopBackStack,
            onNavigateToSignUp = onNavigateToSignUp
        )
    }
}