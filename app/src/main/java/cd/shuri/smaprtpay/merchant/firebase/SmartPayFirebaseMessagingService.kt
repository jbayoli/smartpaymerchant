package cd.shuri.smaprtpay.merchant.firebase

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.utilities.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class SmartPayFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("From: ${remoteMessage.from}")
        val userToken = SmartPayApp.preferences.getString("token", null)
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data playload : ${remoteMessage.data}")
            userToken?.let {
                sendNotification(remoteMessage)
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("fcm", token)
        preferencesEditor.apply()
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(remoteMessage, applicationContext)
    }
}