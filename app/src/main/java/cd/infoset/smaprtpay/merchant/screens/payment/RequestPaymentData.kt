package cd.infoset.smaprtpay.merchant.screens.payment

import java.util.Date

data class RequestPaymentData(
    val amount: Double = 0.0,
    val currency: String = "USD",
    val reason: String = "Test paiment",
    val reference: String = Date().toString(),
    val selectedPaymentMode: PaymentMode = PaymentMode.Card
)
