package cd.shuri.smaprtpay.merchant.utilities

fun phoneNumberFormat(phone: String): String =
    "${phone.substring(0, 4)} ${phone.substring(4, 7)} ${phone.substring(7, 10)} ${phone.substring(
        10,
        13
    )}"