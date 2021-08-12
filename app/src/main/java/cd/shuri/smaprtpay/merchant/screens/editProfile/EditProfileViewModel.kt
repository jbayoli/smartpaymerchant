package cd.shuri.smaprtpay.merchant.screens.editProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class EditProfileViewModel: ViewModel() {
    private val _response = MutableLiveData<CommonResponse>()
    val response : LiveData<CommonResponse> get() = _response
    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader
    private val _navigateTo = MutableLiveData<Boolean?>()
    val navigateTo: LiveData<Boolean?> get() = _navigateTo
    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError
    private val _sectors = MutableLiveData(listOf<SectorsResponse>())
    val sectors :LiveData<List<SectorsResponse>> get() = _sectors
    private val _communes = MutableLiveData(listOf<Commune>())
    val communes: LiveData<List<Commune>> get() = _communes
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        getSectors()
        getAllCommunes()
    }

    fun updateProfile(request: UpdateProfile){
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.updateProfileAsync(auth, request)
                _showDialogLoader.value = false
                Timber.d("$result")
                if (result.status == "0") {
                    _navigateTo.value = true
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

    private fun getSectors() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getSectorsAsync(auth)
                Timber.d("Sectors are ${result.size}")
                if (result.isNotEmpty()) {
                    _sectors.value = result
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                Timber.d(e)
                if (e is ConnectException) {
                    _showTToastForError.value = true
                }
            }
        }
    }

    private fun getAllCommunes() {
        viewModelScope.launch {
            try {
                val result = SmartPayApi.smartPayApiService.getCommuneAsync()
                _showDialogLoader.value = false
                Timber.d("$result")
                if (result.isNotEmpty()) {
                    _communes.value = result
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                Timber.e(e)
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

    fun navigateToDone() {
        _navigateTo.value = null
    }
}