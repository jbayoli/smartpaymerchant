package cd.shuri.smaprtpay.merchant.screens.addaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodRequest
import cd.shuri.smaprtpay.merchant.network.ProvidersData
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.SmartPayApi2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AddAccountViewModel: ViewModel() {

    private val _accountType = MutableLiveData<Int>()
    val accountType : LiveData<Int> get() = _accountType

    private val _isMobileProviderSelected = MutableLiveData<Boolean>()
    val isMobileProviderSelected: LiveData<Boolean> get() = _isMobileProviderSelected

    private val _isCardProviderSelected = MutableLiveData<Boolean>()
    val isCardProviderSelected: LiveData<Boolean> get() = _isCardProviderSelected

    private val _isPhoneNumberEmpty = MutableLiveData<Boolean>()
    val isPhoneNumberEmpty: LiveData<Boolean> get() = _isPhoneNumberEmpty

    private val _isPhoneNumberValid = MutableLiveData<Boolean>()
    val isPhoneNumberValid: LiveData<Boolean> get() = _isPhoneNumberValid

    private val _isCardNumberEmpty = MutableLiveData<Boolean>()
    val isCardNumberEmpty: LiveData<Boolean> get() = _isCardNumberEmpty

    private val _isCardNameValid = MutableLiveData<Boolean>()
    val isCardNameValid: LiveData<Boolean> get() = _isCardNameValid

    private val _isExpirationValid = MutableLiveData<Boolean>()
    val isExpirationValid: LiveData<Boolean> get() = _isExpirationValid

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _showToastSuccess = MutableLiveData<Boolean>()
    val  showToastSuccess : LiveData<Boolean> get() = _showToastSuccess

    private val _showToastError = MutableLiveData<Boolean>()
    val  showToastError : LiveData<Boolean> get() = _showToastError

    private val _messageAddAccount = MutableLiveData<String>()
    val  messageAddAccount : LiveData<String> get() = _messageAddAccount

    private val _providers = MutableLiveData<List<ProvidersData>>()
    val providers: LiveData<List<ProvidersData>> get() = _providers

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastSuccessDone() {
        _showToastSuccess.value = null
    }

    fun showToastErrorDone() {
        _showToastError.value = null
    }

    fun validateFormMobile(request: AddPaymentMethodRequest) : Boolean {
        var valid = true
        if (request.operator.isNotEmpty()) {
            _isMobileProviderSelected.value = true
        } else {
            _isMobileProviderSelected.value = false
            valid = false
        }

        if (request.phone!!.isEmpty()) {
            _isPhoneNumberEmpty.value = true
            valid = false
        } else {
            _isPhoneNumberEmpty.value = false
            if (request.phone.length in 1..8) {
                _isPhoneNumberValid.value = false
                valid = false
            } else {
                _isPhoneNumberValid.value = true
            }
        }

        return valid
    }

    fun validateFormCard(request: AddPaymentMethodRequest) : Boolean {
        var valid = true
        if (request.operator.isNotEmpty()) {
            _isCardProviderSelected.value = true
        } else {
            _isCardProviderSelected.value = false
            valid = false
        }

        if (request.card!!.isEmpty()) {
            _isCardNumberEmpty.value = true
            valid = false
        } else {
            _isCardNumberEmpty.value = false
        }

        if (request.cardName.isNullOrEmpty()) {
            _isCardNameValid.value = false
            valid = false
        } else {
            _isCardNameValid.value = true
        }

        if (request.expiration?.length!! < 4) {
            _isExpirationValid.value = false
            valid = false
        } else {
            _isExpirationValid.value = true
        }

        return valid
    }

    private fun getProviders(authorization: String) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getMobileProvidersAsync(authorization, accountType.value.toString()).await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    for (element in result) {
                        Timber.d("code: ${element.code}\nid: ${element.id}\nname: ${element.name}")
                    }
                    _providers.value = result
                }
            } catch (e : Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun addPaymentMethod(request: AddPaymentMethodRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi2.smartPayApiService.addPaymentMethodAsync(auth, request).await()
                _showDialogLoader.value = false
                Timber.d("Message: ${result.message} status: ${result.status}")
                if (result.status == "0") {
                    _messageAddAccount.value = result.message
                    _showToastSuccess.value = true
                    _navigateToHome.value = true
                } else {
                    _messageAddAccount.value = result.message
                    _showToastError.value = true
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun navigateToHomeDone(){
        _navigateToHome.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun setAccountType(accountTypeSelected: Int) {
        _accountType.value = accountTypeSelected
        Timber.d(auth)
        getProviders(auth)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}