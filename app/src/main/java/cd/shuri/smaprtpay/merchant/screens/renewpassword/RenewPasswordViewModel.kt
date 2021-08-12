package cd.shuri.smaprtpay.merchant.screens.renewpassword

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

class RenewPasswordViewModel : ViewModel() {
    private val _responseStatus = MutableLiveData<String?>()
    val responseStatus: LiveData<String?> get() = _responseStatus

    private val _responseData = MutableLiveData<CommonResponse>()
    val responseData: LiveData<CommonResponse> get() = _responseData

    private val _isOldPasswordEmpty = MutableLiveData<Boolean>()
    val isOldPasswordEmpty: LiveData<Boolean> get() = _isOldPasswordEmpty

    private val _isNewPasswordEmpty = MutableLiveData<Boolean>()
    val isNewPasswordEmpty: LiveData<Boolean> get() = _isNewPasswordEmpty

    private val _isPasswordMatches = MutableLiveData<Boolean>()
    val isPasswordMatches: LiveData<Boolean> get() = _isPasswordMatches

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val _response = MutableLiveData<ForgottenPINResponse>()
    val response : LiveData<ForgottenPINResponse> get() = _response

    private val _navigateToSignIn = MutableLiveData<Boolean?>()
    val navigateToSignIn: LiveData<Boolean?> get() = _navigateToSignIn

    private val _showMessage = MutableLiveData<String?>()
    val showMessage : LiveData<String?> get() = _showMessage

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    fun changePassword(request: EditPasswordRequestData) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.editPasswordAsync(auth, request)
                _showDialogLoader.value = false
                _responseData.value = result
                Timber.d("message: ${result.message} status: ${result.status}")
                if (result.status == "0") {
                    _responseStatus.value = result.status
                    _showMessage.value = result.message
                } else {
                    _responseStatus.value = result.status
                    _showMessage.value = result.message
                }
            } catch (e: Exception) {
                Timber.e("${e.cause}")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun newPinRequest(request: ForgottenPINRequestThree) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.forgottenPINAsync(
                    ForgottenPinStep.StepTree,
                    request
                )
                _response.value = result
                Timber.d("Message: ${result.message}, Status: ${result.status}")
                _showDialogLoader.value = false
                if (result.status == "0") {
                    _navigateToSignIn.value = true
                    _showMessage.value = result.message
                }else {
                    _showMessage.value = result.message
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _showTToastForError.value = true
                Timber.e("$e")
            }
        }
    }

    fun showToastDone() {
        _responseStatus.value = null
    }

    fun validateForm(request: EditPasswordRequestData, isPINForgotten: Boolean = false): Boolean {
        var valid = true

        if (request.oldPassword.isEmpty()) {
            _isOldPasswordEmpty.value = true
            valid = false
        } else {
            _isOldPasswordEmpty.value = false
        }

        if (request.newPassword.isEmpty()) {
            _isNewPasswordEmpty.value = true
            valid = false
        } else {
            _isNewPasswordEmpty.value = false
            if (request.oldPassword == request.newPassword) {
                if (isPINForgotten) {
                    _isPasswordMatches.value = true
                } else {
                    _isPasswordMatches.value = true
                    valid = false
                }
            } else {
                if (isPINForgotten) {
                    _isPasswordMatches.value = false
                    valid = false
                } else {
                    _isPasswordMatches.value = false
                }
            }
        }
        return valid
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun navigateToSignInDone() {
        _navigateToSignIn.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun showMessageDone() {
        _showMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}