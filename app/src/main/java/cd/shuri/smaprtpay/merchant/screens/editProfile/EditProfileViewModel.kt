package cd.shuri.smaprtpay.merchant.screens.editProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileViewModel: ViewModel() {
    private val _response = MutableLiveData<CommonResponse>()
    val response : LiveData<CommonResponse> get() = _response
    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader
    private val _navigateTo = MutableLiveData<Boolean?>()
    val navigateTo: LiveData<Boolean?> get() = _navigateTo
    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError
    private val _sectors = MutableLiveData<List<SectorsResponse>>()
    val sectors :LiveData<List<SectorsResponse>> get() = _sectors
    private val _communes = MutableLiveData<List<Commune>>()
    val communes: LiveData<List<Commune>> get() = _communes
    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)
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
                val result = SmartPayApi.smartPayApiService.updateProfileAsync(auth, request).await()
                _showDialogLoader.value = false
                if (result.status == "0") {
                    _navigateTo.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    private fun getSectors() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getSectorsAsync(auth).await()
                Timber.d("Sectors are ${result.size}")
                if (result.isNotEmpty()) {
                    _sectors.value = result
                } else {
                    _sectors.value = ArrayList()
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _sectors.value = ArrayList()
                _showTToastForError.value = true
                Timber.d("$e")
            }
        }
    }

    private fun getAllCommunes() {
        viewModelScope.launch {
            try {
                val result = SmartPayApi.smartPayApiService.getCommuneAsync().await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _communes.value = result
                } else {
                    _communes.value = listOf()
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _showTToastForError.value = true
                Timber.e("$e")
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}