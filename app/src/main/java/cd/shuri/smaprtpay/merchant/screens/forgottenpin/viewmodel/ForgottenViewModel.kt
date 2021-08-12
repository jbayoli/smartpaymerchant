package cd.shuri.smaprtpay.merchant.screens.forgottenpin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.network.ForgottenPINRequestOneTwo
import cd.shuri.smaprtpay.merchant.network.ForgottenPINResponse
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.launch
import timber.log.Timber

class ForgottenViewModel: ViewModel() {

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    private val _response = MutableLiveData<ForgottenPINResponse?>()
    val response: LiveData<ForgottenPINResponse?> get() = _response

    private val _isCodeEmpty = MutableLiveData<Boolean?>()
    val isCodeEmpty: LiveData<Boolean?> get() = _isCodeEmpty

    private val _showToast = MutableLiveData<Boolean?>()
    val showToast: LiveData<Boolean?> get() = _showToast

    private val _isCodeCorrect = MutableLiveData<Boolean?>()
    val isCodeCorrect: LiveData<Boolean?> get() = _isCodeCorrect

    private val _navigateTo = MutableLiveData<Boolean?>()
    val navigateTo: LiveData<Boolean?> get() = _navigateTo


    fun validateForm(code: String) : Boolean{
        var isValid = true
        if (code.isEmpty()) {
            isValid = false
            _isCodeEmpty.value = true
        } else {
            _isCodeEmpty.value = false
        }
        return isValid
    }


    fun sendRequest(code: String, step: String) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.forgottenPINAsync(step, ForgottenPINRequestOneTwo(code))
                Timber.d("$result")
                _response.value = result
                _showDialogLoader.value = false
                if (result.status!! == "0") {
                    _navigateTo.value = true
                    _isCodeCorrect.value = true
                } else {
                    _isCodeCorrect.value = false
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _showToast.value = true
                Timber.e("$e")
                e.printStackTrace()
            }
        }
    }

    fun showDialogDone() {
        _showDialogLoader.value = null
    }

    fun showToastDone() {
        _showToast.value = null
    }

    fun reinitCode() {
        _isCodeCorrect.value = null
        _isCodeEmpty.value = null
    }

    fun navigateToDone() {
        _navigateTo.value = null
        _response.value = null
    }
}