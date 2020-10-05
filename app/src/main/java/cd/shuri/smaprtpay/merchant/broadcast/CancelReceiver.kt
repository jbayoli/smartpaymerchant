package cd.shuri.smaprtpay.merchant.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionValidationRequest
import cd.shuri.smaprtpay.merchant.utilities.EXTRA_CODE
import cd.shuri.smaprtpay.merchant.utilities.cancelNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class CancelReceiver: BroadcastReceiver() {

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"
    private val fcm = SmartPayApp.preferences.getString("fcm", "")

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Transaction ${intent?.getStringExtra(EXTRA_CODE)} canceled")
        cancelTrans(intent?.getStringExtra(EXTRA_CODE)!!)
        val notificationManager = ContextCompat.getSystemService(context!!, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotification()
    }

    private suspend fun cancelTransaction(code: String) {
        withContext(Dispatchers.IO) {
            SmartPayApi.smartPayApiService.validateTransactionAsync(auth, TransactionValidationRequest(
                code,
                userCode!!,
                false,
                fcm!!
            )).await()
        }
    }

    private fun cancelTrans(code: String) {
        GlobalScope.launch {
            try {
                cancelTransaction(code)
            } catch (e: Exception) {
                Timber.d("$e")
            }
        }
    }
}