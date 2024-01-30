package cd.infoset.smaprtpay.merchant.screens.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cd.infoset.smaprtpay.merchant.screens.Screen

fun NavGraphBuilder.homeScreen(
    onNavigateToLogIn: () -> Unit,
) {
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToLogin = onNavigateToLogIn
        )
    }
}