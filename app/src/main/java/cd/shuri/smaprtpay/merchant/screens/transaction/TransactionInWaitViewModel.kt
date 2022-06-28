package cd.shuri.smaprtpay.merchant.screens.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import cd.shuri.smaprtpay.merchant.network.TransactionType
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class TransactionInWaitViewModel : ViewModel() {

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    val transactions = Pager(PagingConfig(pageSize = 10)) {
        TransactionsPagingSource(TransactionType.Pending, auth, userCode!!)
    }.flow.cachedIn(viewModelScope)

    fun showDialogLoaderDone(isLoading: Boolean? = null) {
        _showDialogLoader.value = isLoading
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }
}