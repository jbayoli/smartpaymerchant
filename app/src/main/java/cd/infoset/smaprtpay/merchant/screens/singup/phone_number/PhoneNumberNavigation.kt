package cd.infoset.smaprtpay.merchant.screens.singup.phone_number

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.phoneNumberScreen(
    onPopBackStack: () -> Unit,
    onNavigateToAuthenticate: () -> Unit,
) {
    composable(Screen.PhoneNumber.route) {
        PhoneNumberScreen(
            onPopBackStack = onPopBackStack,
            onNavigateToAuthenticate = onNavigateToAuthenticate
        )
    }
}