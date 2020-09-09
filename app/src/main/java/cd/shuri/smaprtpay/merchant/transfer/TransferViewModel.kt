package cd.shuri.smaprtpay.merchant.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.Transfers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class TransferViewModel: ViewModel() {
    private val _transfers = MutableLiveData<List<Transfers>>()
    val transfers: LiveData<List<Transfers>> get() = _transfers

    private val _isTransferDone = MutableLiveData<Boolean>()
    val isTransferDone: LiveData<Boolean> get() = _isTransferDone

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        _transfers.value = ArrayList()
        _isTransferDone.value = false
        getTransfers()
    }

    private fun getTransfers() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result =
                    SmartPayApi.smartPayApiService.getTransfersAsync(userCode!!, auth).await()
                _showDialogLoader.value = false
                Timber.d("Transfers => ${result.transactions.size}")
                if (result.transactions.isNotEmpty()) {
                    _isTransferDone.value = true
                    _transfers.value = result.transactions
                } else {
                    _isTransferDone.value = false
                    _transfers.value = ArrayList()
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _transfers.value = ArrayList()
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