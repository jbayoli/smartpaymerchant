package cd.shuri.smaprtpay.merchant.screens.signupsecond

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.RegisterRequest
import cd.shuri.smaprtpay.merchant.network.RegisterStep
import cd.shuri.smaprtpay.merchant.network.SectorsResponse
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.*
import timber.log.Timber

class SignUpSecondViewModel: ViewModel() {
    private val _isActivityEmpty= MutableLiveData<Boolean>()
    val isActivityEmpty : LiveData<Boolean> get() = _isActivityEmpty

    private val _isSectorSelected= MutableLiveData<Boolean>()
    val isSectorSelected : LiveData<Boolean> get() = _isSectorSelected

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _showToastSuccess = MutableLiveData<Boolean?>()
    val  showToastSuccess : LiveData<Boolean?> get() = _showToastSuccess

    private val _showToastError = MutableLiveData<Boolean?>()
    val  showToastError : LiveData<Boolean?> get() = _showToastError

    private val _messageRegister = MutableLiveData<String>()
    val  messageRegister : LiveData<String> get() = _messageRegister

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome : LiveData<Boolean?> get() = _navigateToHome

    private val _sectors = MutableLiveData<List<SectorsResponse>>()
    val sectors :LiveData<List<SectorsResponse>> get() = _sectors

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        _sectors.value = ArrayList()
        getSectors()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastSuccessDone() {
        _showToastSuccess.value = null
    }

    fun showToastErrorDone() {
        _showToastError.value = null
    }

    fun validateForm(request: RegisterRequest) : Boolean {
        var valid = true
        if (request.activity.isEmpty()) {
            _isActivityEmpty.value = true
            valid = false
        } else {
            _isActivityEmpty.value = false
        }

        if (request.sector.isEmpty()) {
            _isSectorSelected.value = false
            valid = false
        } else {
            _isSectorSelected.value = true
        }

        return valid
    }

    private fun getSectors() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getSectorsAsync(auth)
                _showDialogLoader.value = false
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

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                Timber.d("$request")
                val result = SmartPayApi.smartPayApiService.combinedRegisterRequest(
                    RegisterStep.StepFour,
                    request
                )
                _showDialogLoader.value = false
                Timber.d("message: ${result.message} status: ${result.status}")
                if (result.status == "0") {
                    registrationDone()
                    _messageRegister.value = result.message
                    _showToastSuccess.value = true
                    _navigateToHome.value = true
                } else {
                    _messageRegister.value = result.message
                    _showToastError.value = true
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    private suspend fun registrationDone() {
        withContext(Dispatchers.Main) {
            try {
                val preferencesEditor = SmartPayApp.preferences.edit()
                preferencesEditor.remove("isRegistrationDone")
                preferencesEditor.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}