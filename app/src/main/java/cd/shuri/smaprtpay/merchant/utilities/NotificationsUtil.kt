package cd.shuri.smaprtpay.merchant.utilities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import cd.shuri.smaprtpay.merchant.MainActivity
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.broadcast.CancelReceiver
import cd.shuri.smaprtpay.merchant.broadcast.ValidationReceiver
import com.google.firebase.messaging.RemoteMessage

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val REQUEST_CODE_TWO = 1

const val EXTRA_CODE = "cd.infoset.smartpay.merchant.CODE"
const val ACTION_SHOW_VALIDATE_FRAGMENT = "cd.infoset.smartpay.merchant.ACTION_SHOW_VALIDATE_FRAGMENT"

fun NotificationManager.sendNotification(remoteMessage: RemoteMessage, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
        action = ACTION_SHOW_VALIDATE_FRAGMENT
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val validationReceiver = Intent(applicationContext, ValidationReceiver::class.java).also {
        it.putExtra(EXTRA_CODE, "${remoteMessage.data["code"]}")
    }
    val cancelReceiver = Intent(applicationContext, CancelReceiver::class.java).also {
        it.putExtra(EXTRA_CODE, "${remoteMessage.data["code"]}")
    }


    val actionValidation = PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, validationReceiver, PendingIntent.FLAG_UPDATE_CURRENT)
    val actionCancel = PendingIntent.getBroadcast(applicationContext, REQUEST_CODE_TWO, cancelReceiver, PendingIntent.FLAG_UPDATE_CURRENT)


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_chanel_id)
    )
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(remoteMessage.data["title"])
        .setContentText(remoteMessage.data["content"])
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setColor(Color.parseColor("#003c8f"))
        .addAction(R.drawable.logo, "VALIDER", actionValidation)
        .addAction(R.drawable.logo, "ANNULER", actionCancel)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}