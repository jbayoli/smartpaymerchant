package cd.shuri.smaprtpay.merchant.transactionvalidation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class TransactionValidationViewModel: ViewModel() {
    private val _transactions =  MutableLiveData(listOf<TransactionResponse>())
    val transactions : LiveData<List<TransactionResponse>> get() = _transactions

    private val _validation = MutableLiveData<CommonResponse>()
    val validation : LiveData<CommonResponse> get() = _validation

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome : LiveData<Boolean?> get() = _navigateToHome

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val _indexRemoved= MutableLiveData<Int>()
    val indexRemoved: LiveData<Int> get() = _indexRemoved

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        Timber.d(auth)
        Timber.d(userCode)
        getTransactionsToValidate()
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

    fun getTransactionsToValidate() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getTransactionByTypeAsync(
                    authorization = auth,
                    type = TransactionType.ToValidate,
                    customer = userCode!!
                )
                _showDialogLoader.value = false
                Timber.d("$result")
                if (result.isNotEmpty()) {
                    _transactions.value = result
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

    fun validateTransaction(request: TransactionValidationRequest, transaction: TransactionResponse) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.validateTransactionAsync(auth, request)
                _validation.value = result
                _showDialogLoader.value = false
                if (result.status == "0" || result.status == "1") {
                    val transacs = _transactions.value!!.toMutableList()
                    transacs.remove(transaction)
                    _indexRemoved.value =  transacs.indexOf(transaction)
                    _transactions.value = transacs.toList()
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

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }
}