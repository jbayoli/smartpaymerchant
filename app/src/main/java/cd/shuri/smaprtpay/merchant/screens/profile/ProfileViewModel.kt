package cd.shuri.smaprtpay.client.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.Profile
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel: ViewModel() {
    private val _response = MutableLiveData<Profile>()
    val response: LiveData<Profile> get() = _response
    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader
    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getProfileInfo()
    }

    private fun getProfileInfo() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getInfoAsync(userCode!!, auth).await()
                _response.value = result
                _showDialogLoader.value = false
                Timber.d("$result")
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _showTToastForError.value = true
                e.printStackTrace()
            }

        }
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}