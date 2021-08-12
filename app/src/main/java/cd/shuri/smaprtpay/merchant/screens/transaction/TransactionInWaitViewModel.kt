package cd.shuri.smaprtpay.merchant.screens.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import cd.shuri.smaprtpay.merchant.network.TransactionType
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class TransactionInWaitViewModel : ViewModel() {
    private val _transactions =  MutableLiveData(listOf<TransactionResponse>())
    val transactions : LiveData<List<TransactionResponse>> get() = _transactions

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        getTransactions()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    private fun getTransactions() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getTransactionByTypeAsync(
                    authorization = auth,
                    type = TransactionType.Pending,
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

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }
}