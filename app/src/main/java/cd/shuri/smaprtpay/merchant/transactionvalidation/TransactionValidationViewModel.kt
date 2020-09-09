package cd.shuri.smaprtpay.merchant.transactionvalidation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.CommonResponse
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

    private val _validation = MutableLiveData<CommonResponse>()
    val validation : LiveData<CommonResponse> get() = _validation

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private val _indexRemoved= MutableLiveData<Int>()
    val indexRemoved: LiveData<Int> get() = _indexRemoved

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

    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun getTransactions() {
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

    fun validateTransaction(request: TransactionValidationRequest, transaction: TransactionResponse) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.validateTransactionAsync(auth, request).await()
                _validation.value = result
                _showDialogLoader.value = false
                if (result.status == "0" || result.status == "1") {
                    val transacs = _transactions.value!!.toMutableList()
                    _indexRemoved.value =  transacs.indexOf(transaction)
                    transacs.remove(transaction)
                    _transactions.value = transacs.toList()
                }
            } catch (e: Exception) {
                Timber.e("$e")
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