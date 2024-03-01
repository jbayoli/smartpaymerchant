package cd.infoset.smaprtpay.merchant.screens

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object PhoneNumber : Screen("phone_number_screen")
    data object Home : Screen("home_screen")
    data object ValidateOtp : Screen("validate_otp_screen")
    data object SignUp : Screen("sign_up_screen")
    data object RequestPayment: Screen("request_payment_screen")
    data object TapToPay: Screen("tap_to_pay_screen")
}
