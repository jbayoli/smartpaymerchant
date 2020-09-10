package cd.shuri.smaprtpay.merchant.screens.editpaymentaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class EditPaymentAccountViewModel(account: Int) : ViewModel() {

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

    private val _providers = MutableLiveData<List<ProvidersData>>()
    val providers: LiveData<List<ProvidersData>> get() = _providers

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private val _response = MutableLiveData<CommonResponse>()
    val response : LiveData<CommonResponse> get() = _response

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    private var accountType = ""

    init {
        accountType = account.toString()
        getProviders(auth)
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
                val result = SmartPayApi.smartPayApiService.getMobileProvidersAsync(authorization, accountType).await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _providers.value = result
                }
            } catch (e : Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun editPaymentMethod(request: AddPaymentMethodRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi2.smartPayApiService.editPaymentMethodAsync(auth, request).await()
                _showDialogLoader.value = false
                Timber.d("Message: ${result.message} status: ${result.status}")
                _response.value = result
                if (result.status == "0") {
                    _navigateToHome.value = true
                }
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun navigateToHomeDone(){
        _navigateToHome.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}