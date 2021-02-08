package cd.shuri.smaprtpay.merchant.extentions

fun String.hideCardNumber(): String {
    var stars = ""
    for (e in this.lastIndex.minus(3) downTo 0) {
        stars += "#"
    }
    return this.replaceRange(0, this.lastIndex.minus(3), stars)
}