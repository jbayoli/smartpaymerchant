package cd.shuri.smaprtpay.merchant.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.DashboardResponse
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class HomeViewModel : ViewModel() {

    private val _response = MutableLiveData<DashboardResponse>()
    val response: LiveData<DashboardResponse> get() = _response

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"


    init {
        savePreference()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun getDashBoardData() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result =
                    SmartPayApi.smartPayApiService.getDashBoardDataAsync(auth, userCode!!)
                _showDialogLoader.value = false
                Timber.d("$result")
                _response.value = result

            } catch (e: Exception) {
                Timber.e(e)
                _showDialogLoader.value = false
                if (e is ConnectException) {
                    _showTToastForError.value = true
                }
            }
        }
    }

    private fun savePreference() {
        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putInt("step", 0)
        preferencesEditor.apply()
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }
}