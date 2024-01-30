package cd.infoset.smartpay.client.screens.singup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.singUpScreen(
    onPopBackStack: () -> Unit,
) {
    composable(Screen.SignUp.route) {
        SignUpScreen(
            onPopBackStack = onPopBackStack
        )
    }
}