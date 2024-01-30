package cd.infoset.smaprtpay.merchant.screens.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.loginScreen(
    onPopBackStack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    composable(Screen.Login.route) {
        LoginScreen(
            onPopBackStack = onPopBackStack,
            onNavigateToSignUp = onNavigateToSignUp
        )
    }
}