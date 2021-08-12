package cd.shuri.smaprtpay.merchant.screens.accounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class AccountsViewModel : ViewModel() {

    private val _paymentMethods = MutableLiveData(mutableListOf<AccountsResponse>())
    val paymentMethods: LiveData<MutableList<AccountsResponse>> get() = _paymentMethods

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val _deleteResponse = MutableLiveData<CommonResponse>()
    val deleteResponse: LiveData<CommonResponse> get() = _deleteResponse

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    var indexOfRemovedAccount = 0

    init {
        getPaymentMethods()
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    private fun getPaymentMethods() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getPaymentMethodsAsync(
                    auth,
                    userCode!!
                )
                Timber.d("$result")
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _paymentMethods.value = result.toMutableList()
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

    fun deletePaymentAccount(account: AccountsResponse) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.addEditOrDeletePaymentMethodAsync(
                    authorization = auth,
                    action = PaymentMethodAction.Delete,
                    request = DeletePaymentAccount(
                        account.code,
                        userCode!!
                    )
                )
                _showDialogLoader.value = false
                if (result.status == "0") {
                    indexOfRemovedAccount = _paymentMethods.value?.indexOf(account)!!
                    _paymentMethods.value?.remove(account)
                }
                _deleteResponse.value = result
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