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

fun validateCreditCardNumber(str: String): Boolean {
    val ints = IntArray(str.length)
    for (i in str.indices) {
        ints[i] = str.substring(i, i + 1).toInt()
    }
    var i = ints.size - 2
    while (i >= 0) {
        var j = ints[i]
        j *= 2
        if (j > 9) {
            j = j % 10 + 1
        }
        ints[i] = j
        i -= 2
    }
    var sum = 0
    for (index in ints.indices) {
        sum += ints[index]
    }
    return sum % 10 == 0
}