package cd.shuri.smaprtpay.merchant.screens.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.Notification
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>?>()
    val notifications: LiveData<List<Notification>?> get() = _notifications

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        _notifications.value = ArrayList()
        getNotifications()
    }

    private fun getNotifications() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result =
                    SmartPayApi.smartPayApiService.getNotificationsAsync(auth,userCode!!)
                _showDialogLoader.value = false
                Timber.d(" => ${result.notifications?.size}")
                if (!result.notifications.isNullOrEmpty()) {
                    _notifications.value = result.notifications
                }
            } catch (e: Exception) {
                Timber.e(e)
                _showDialogLoader.value = false
                if (e is ConnectException) {
                    _showTToastForError.value = true
                }
            }
        }
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }
}