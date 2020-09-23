package cd.shuri.smaprtpay.merchant.screens.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class TransactionsErrorViewModel : ViewModel() {
    private val _transactions =  MutableLiveData<List<TransactionResponse>>()
    val transactions : LiveData<List<TransactionResponse>> get() = _transactions

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        _transactions.value = ArrayList()
        getTransactions()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    private fun getTransactions() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getAllTransactionErrorAsync(auth, userCode!!).await()
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
                _showDialogLoader.value = true
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