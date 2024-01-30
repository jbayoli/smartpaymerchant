package cd.infoset.smaprtpay.merchant

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cd.infoset.smaprtpay.merchant.screens.Screen
import cd.infoset.smaprtpay.merchant.screens.home.homeScreen
import cd.infoset.smaprtpay.merchant.screens.login.loginScreen
import cd.infoset.smaprtpay.merchant.screens.singup.phone_number.phoneNumberScreen
import cd.infoset.smartpay.client.screens.singup.singUpScreen
import cd.infoset.smaprtpay.merchant.screens.singup.validate_otp.validateOptScreen

@Composable
fun FlexPayApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn() + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        popEnterTransition = {
            fadeIn() + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        }
    ) {
        homeScreen(
            onNavigateToLogIn = {
                navController.navigate(Screen.Login.route)
            }
        )
        loginScreen(
            onPopBackStack = {
                navController.popBackStack()
            },
            onNavigateToSignUp = {
                navController.navigate(Screen.PhoneNumber.route)
            }
        )
        phoneNumberScreen(
            onPopBackStack = {
                navController.popBackStack()
            },
            onNavigateToAuthenticate = {
                navController.navigate(Screen.ValidateOtp.route)
            }
        )
        validateOptScreen(
            onPopBackStack = {
                navController.popBackStack()
            },
            onNavigateToSignUp = {
                navController.navigate(Screen.SignUp.route)
            }
        )

        singUpScreen(
            onPopBackStack = { navController.popBackStack() }
        )
    }
}