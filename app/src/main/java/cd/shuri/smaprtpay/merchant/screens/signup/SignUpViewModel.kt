package cd.shuri.smaprtpay.merchant.screens.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SingUpRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class SignUpViewModel : ViewModel() {

    private val _isCodeEmpty = MutableLiveData<Boolean>()
    val isCodeEmpty : LiveData<Boolean> get() = _isCodeEmpty

    private val _isUserNameEmpty = MutableLiveData<Boolean>()
    val isUserNameEmpty : LiveData<Boolean> get() = _isUserNameEmpty

    private val _isPasswordEmpty = MutableLiveData<Boolean>()
    val isPasswordEmpty : LiveData<Boolean> get() = _isPasswordEmpty

    private val _isConfirmedPasswordEmpty = MutableLiveData<Boolean>()
    val isConfirmedPasswordEmpty : LiveData<Boolean> get() = _isConfirmedPasswordEmpty

    private val _isPasswordsMatches = MutableLiveData<Boolean>()
    val isPasswordsMatches : LiveData<Boolean> get() = _isPasswordsMatches

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog : LiveData<Boolean> get() = _showDialog

    private val _isUserNameExist = MutableLiveData<Boolean>()
    val isUserNameExist : LiveData<Boolean> get() = _isUserNameExist

    private val _isMerchantCodeExist = MutableLiveData<Boolean>()
    val isMerchantCodeExist : LiveData<Boolean> get() = _isMerchantCodeExist

    private val _navigateTo = MutableLiveData<Boolean>()
    val navigateTo : LiveData<Boolean> get() = _navigateTo

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun validateForm(request: SingUpRequest) : Boolean {
        var valid = true

        if (request.username.isEmpty()) {
            _isUserNameEmpty.value = true
            valid = false
        } else {
            _isUserNameEmpty.value = false
        }

        if (request.code.isEmpty()) {
            _isCodeEmpty.value = true
            valid = false
        } else {
            _isCodeEmpty.value = false
        }

        if (request.password.isEmpty()) {
            _isPasswordEmpty.value = true
            valid = false
        } else {
            _isPasswordEmpty.value = false
        }

        if (request.confirmedPassword.isEmpty()) {
            _isConfirmedPasswordEmpty.value = true
            valid = false
        } else {
            _isConfirmedPasswordEmpty.value = false
            if (request.confirmedPassword == request.password) {
                _isPasswordsMatches.value = true
            } else {
                _isPasswordsMatches.value = false
                valid = false
            }
        }

        return valid
    }

    fun signUp(request: SingUpRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.signUpAsync(request).await()
                Timber.d("message : ${result.message} status: ${result.status}")
                _showDialogLoader.value = false
                when (result.status) {
                    "0" -> {
                        _isUserNameExist.value = false
                        _isMerchantCodeExist.value = false
                        Timber.d("user_code : ${result.customer}\n token: ${result.token}\n fcm: ${request.fcm}")
                        val preferencesEditor = SmartPayApp.preferences.edit()
                        preferencesEditor.putString("user_code", result.customer!!)
                        preferencesEditor.putString("token", result.token!!)
                        preferencesEditor.putString("fcm", request.fcm)
                        preferencesEditor.apply()
                        _showDialog.value = true
                    }
                    "2" -> {
                        _isUserNameExist.value = true
                    }
                    "3" -> {
                        _isMerchantCodeExist.value = true
                    }
                }
            } catch (e: Exception) {
                Timber.e("$e")
            }
        }
    }

    fun showDialogDone() {
        _showDialog.value = null
    }

    fun navigate(isNavigateReady: Boolean) {
        _navigateTo.value = isNavigateReady
    }

    fun navigationDone() {
        _navigateTo.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}