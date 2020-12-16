package cd.shuri.smaprtpay.merchant.utilities

fun phoneNumberFormat(phone: String): String =
    "${phone.substring(0, 4)} ${phone.substring(4, 7)} ${phone.substring(7, 10)} ${phone.substring(
        10,
        13
    )}"

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