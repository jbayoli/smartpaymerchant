package cd.shuri.smaprtpay.merchant.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder

fun phoneNumberFormat(phone: String): String =
    "${phone.substring(0, 4)} ${phone.substring(4, 7)} ${phone.substring(7, 10)} ${phone.substring(
        10,
        13
    )}"

@Suppress("DEPRECATION")
fun generateQRCode(inputValue: String, context: Context): Bitmap {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display
    } else {
        windowManager.defaultDisplay
    }
    val point = Point()
    display?.getRealSize(point)

    val width = point.x
    val height = point.y
    var smallerDimension = if (width < height) {
        width
    } else {
        height
    }

    smallerDimension = smallerDimension * 3 / 4

    val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension)
    qrgEncoder.colorBlack = Color.parseColor("#003c8f")
    qrgEncoder.colorWhite = Color.parseColor("#edeff1")

    return qrgEncoder.bitmap
}