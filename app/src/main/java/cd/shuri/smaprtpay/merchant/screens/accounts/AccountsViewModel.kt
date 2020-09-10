package cd.shuri.smaprtpay.merchant.screens.accounts

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

class AccountsViewModel : ViewModel() {

    private val _paymentMethods = MutableLiveData<List<AccountsResponse>>()
    val paymentMethods: LiveData<List<AccountsResponse>> get() = _paymentMethods

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private val _deleteResponse = MutableLiveData<CommonResponse>()
    val deleteResponse: LiveData<CommonResponse> get() = _deleteResponse

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"


    init {
        _paymentMethods.value = ArrayList()
        getPaymentMethods()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    private fun getPaymentMethods() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result =
                    SmartPayApi2.smartPayApiService.getPaymentMethodsAsync(auth, userCode!!).await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _paymentMethods.value = result
                    for (element in result) {
                        Timber.d("code: ${element.code}")
                    }
                } else {
                    _paymentMethods.value = ArrayList()
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _paymentMethods.value = ArrayList()
                _showTToastForError.value = true
            }
        }
    }

    fun deletePaymentAccount(accountCode: String) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi2.smartPayApiService.deletePaymentAccountAsync(
                    auth,
                    DeletePaymentAccount(accountCode, userCode)
                ).await()
                _showDialogLoader.value = false
                _deleteResponse.value = result
            } catch (e: Exception) {
                Timber.d("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}