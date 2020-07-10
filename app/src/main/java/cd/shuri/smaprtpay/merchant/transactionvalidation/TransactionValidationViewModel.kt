package cd.shuri.smaprtpay.merchant.transactionvalidation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import cd.shuri.smaprtpay.merchant.network.TransactionValidationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class TransactionValidationViewModel: ViewModel() {
    private val _transactions =  MutableLiveData<List<TransactionResponse>>()
    val transactions : LiveData<List<TransactionResponse>> get() = _transactions

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _showToast = MutableLiveData<Boolean>()
    val  showToast : LiveData<Boolean> get() = _showToast

    private val _messageValidation = MutableLiveData<String>()
    val  messageValidation : LiveData<String> get() = _messageValidation

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        _transactions.value = ArrayList()
        Timber.d(auth)
        Timber.d(userCode)
        getTransactions()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastDone() {
        _showToast.value = null
    }

    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    private fun getTransactions() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getWaitingTransactionAsync(auth, userCode!!).await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _transactions.value = result
                    for (element in result) {
                        Timber.d("code: ${element.code}")
                    }
                } else {
                    Timber.d("No transactions")
                    _transactions.value = ArrayList()
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _transactions.value = ArrayList()
            }
        }
    }

    fun validateTransaction(request: TransactionValidationRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.validateTransactionAsync(auth, request).await()
                _showDialogLoader.value = false
                if (result.status == "0") {
                    _messageValidation.value = result.message
                    _showToast.value = true
                    _navigateToHome.value = true
                } else if (result.status == "1") {
                    _messageValidation.value = result.message
                    _showToast.value = true
                    _navigateToHome.value = true
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _messageValidation.value = "Http 500"
                _showToast.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}