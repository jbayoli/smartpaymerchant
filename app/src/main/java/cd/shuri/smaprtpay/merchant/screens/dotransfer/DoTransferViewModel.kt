package cd.shuri.smaprtpay.merchant.screens.dotransfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.AccountsResponse
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.SmartPayApi2
import cd.shuri.smaprtpay.merchant.network.TransferRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class DoTransferViewModel: ViewModel() {

    private val _paymentMethods = MutableLiveData<List<AccountsResponse>>()
    val paymentMethods: LiveData<List<AccountsResponse>> get() = _paymentMethods

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader

    private val _transferCardToEMoneyStatus = MutableLiveData<String>()
    val transferCardToEMoneyStatus: LiveData<String> get() = _transferCardToEMoneyStatus

    private val _isAmountValid = MutableLiveData<Boolean>()
    val isAmountValid: LiveData<Boolean> get() = _isAmountValid

    private val _isCurrencySelected = MutableLiveData<Boolean>()
    val isCurrencySelected: LiveData<Boolean> get() = _isCurrencySelected

    private val _isCardSelected = MutableLiveData<Boolean>()
    val isCardSelected: LiveData<Boolean> get() = _isCardSelected

    private val _isMobileSelected = MutableLiveData<Boolean>()
    val isMobileSelected: LiveData<Boolean> get() = _isMobileSelected

    private val _isCvvValid = MutableLiveData<Boolean>()
    val isCvvValid: LiveData<Boolean> get() = _isCvvValid

    private val _navigateToTransfersFragment = MutableLiveData<Boolean>()
    val navigateToTransfersFragment: LiveData<Boolean> get() = _navigateToTransfersFragment

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPaymentMethods()
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
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
            }
        }
    }

    fun validateForm(request: TransferRequest): Boolean {
        var valid = true

        if (request.currency!!.isEmpty()) {
            _isCurrencySelected.value = false
            valid = false
        } else {
            _isCurrencySelected.value = true
        }

        if (request.card!!.isEmpty()) {
            _isCardSelected.value = false
            valid = false
        } else {
            _isCardSelected.value = true
        }

        if (request.mobile!!.isEmpty()) {
            _isMobileSelected.value = false
            valid = false
        } else {
            _isMobileSelected.value = true
        }

        if (request.amount!!.isEmpty()) {
            _isAmountValid.value = false
            valid = false
        } else {
            _isAmountValid.value = true
        }

        if (request.cvv!!.isEmpty()) {
            _isCvvValid.value = false
            valid = false
        } else {
            _isCvvValid.value = true
        }

        return valid
    }

    fun sendMoneyToEMoney(request: TransferRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.cardToEMoneyAsync(auth, request).await()
                _showDialogLoader.value = false
                _transferCardToEMoneyStatus.value = result.status
                if (result.status == "0") {
                    _navigateToTransfersFragment.value = true
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showTToastForError.value = true
                _showDialogLoader.value = false
            }
        }
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun navigateToTransfersFragmentDone() {
        _navigateToTransfersFragment.value = true
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}