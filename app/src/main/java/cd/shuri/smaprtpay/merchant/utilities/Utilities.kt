package cd.shuri.smaprtpay.merchant.utilities

fun phoneNumberFormat(phone: String): String {
    var startIndex = 0
    var endIndex : Int
    var phoneNumberFormat = ""
    while (startIndex < phone.length) {
        if (startIndex == 0) {
            endIndex = startIndex.plus(4)
            phoneNumberFormat = phone.substring(startIndex, endIndex)
            startIndex += 4
        } else {
            endIndex = startIndex.plus(3)
            phoneNumberFormat += " ${phone.substring(startIndex, endIndex)}"
            startIndex += 3
        }
    }
    return phoneNumberFormat
}