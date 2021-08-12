package cd.shuri.smaprtpay.merchant.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.Profile
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class ProfileViewModel: ViewModel() {
    private val _response = MutableLiveData<Profile>()
    val response: LiveData<Profile> get() = _response
    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader
    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"


    init {
        getProfileInfo()
    }

    private fun getProfileInfo() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getInfoAsync(userCode!!, auth)
                _response.value = result
                _showDialogLoader.value = false
                Timber.d("$result")
            } catch (e: Exception) {
                Timber.e(e)
                _showDialogLoader.value = false
                if (e is ConnectException) {
                    _showTToastForError.value = true
                }
            }

        }
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }
}