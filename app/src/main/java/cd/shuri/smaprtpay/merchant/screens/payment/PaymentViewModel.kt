package cd.shuri.smaprtpay.merchant.screens.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.CommonResponse
import cd.shuri.smaprtpay.merchant.network.Payment
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class PaymentViewModel : ViewModel() {
    private val _response = MutableLiveData<CommonResponse>()
    val response: LiveData<CommonResponse> get() = _response
    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?> get() = _navigateToHome
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"
    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader
    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    fun makePayment(request: Payment) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                Timber.i("$request")
                val result = SmartPayApi.smartPayApiService.paymentAsync(auth, request)
                Timber.d("$result")
                _showDialogLoader.value = false
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

    fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }
}