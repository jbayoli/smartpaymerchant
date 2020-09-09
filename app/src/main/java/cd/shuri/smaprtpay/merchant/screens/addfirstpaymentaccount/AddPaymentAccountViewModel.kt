package cd.shuri.smaprtpay.merchant.screens.addfirstpaymentaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodFirstTimeRequest
import cd.shuri.smaprtpay.merchant.network.ProvidersData
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.*
import timber.log.Timber

class AddPaymentAccountViewModel : ViewModel() {
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

    private val _isCardNameEmpty = MutableLiveData<Boolean>()
    val isCardNameEmpty: LiveData<Boolean> get() = _isCardNameEmpty

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

    private val _providersPhone = MutableLiveData<List<ProvidersData>>()
    val providersPhone: LiveData<List<ProvidersData>> get() = _providersPhone

    private val _providersCard = MutableLiveData<List<ProvidersData>>()
    val providersCard: LiveData<List<ProvidersData>> get() = _providersCard

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val userToken = SmartPayApp.preferences.getString("token", "")
    private val auth = "Bearer $userToken"

    init {
        getCardProviders(auth)
        getPhoneProviders(auth)
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastSuccessDone() {
        _showToastSuccess.value = null
    }

    fun showToastErrorDone() {
        _showToastError.value = null
    }

    fun validateForm(request: AddPaymentMethodFirstTimeRequest) : Boolean {
        var valid = true

        if (request.operator1.isNullOrEmpty() && request.operator2.isNullOrEmpty()) {
            valid = false
            _isCardProviderSelected.value = false
            _isMobileProviderSelected.value = false

            _isPhoneNumberEmpty.value = false
            _isPhoneNumberValid.value = true
            _isCardNumberEmpty.value = false
            _isExpirationValid.value = true
            _isCardNameEmpty.value = false
        } else {
            _isCardProviderSelected.value = true
            _isMobileProviderSelected.value = true

            request.operator1?.let {
                if (request.phone.isNullOrEmpty()) {
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
            }

            request.operator2?.let {
                if (request.card.isNullOrEmpty()) {
                    _isCardNumberEmpty.value = true
                    valid = false
                } else {
                    _isCardNumberEmpty.value = false
                }

                if (request.expiration?.length!! < 4) {
                    _isExpirationValid.value = false
                    valid = false
                } else {
                    _isExpirationValid.value = true
                }

                if (request.cardName.isNullOrEmpty()) {
                    _isCardNameEmpty.value = true
                    valid = false
                } else {
                    _isCardNameEmpty.value = false
                }
            }
        }
        return valid
    }

    private fun getPhoneProviders(authorization: String) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getMobileProvidersAsync(authorization, "1").await()
                if (result.isNotEmpty()) {
                    for (element in result) {
                        Timber.d("code: ${element.code}\nid: ${element.id}\nname: ${element.name}")
                    }
                    _providersPhone.value = result
                }
            } catch (e : Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    private fun getCardProviders(authorization: String) {
        viewModelScope.launch {
            try {
                val result = SmartPayApi.smartPayApiService.getMobileProvidersAsync(authorization, "2").await()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    for (element in result) {
                        Timber.d("code: ${element.code}\nid: ${element.id}\nname: ${element.name}")
                    }
                    _providersCard.value = result
                }
            } catch (e : Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun addPaymentMethod(request: AddPaymentMethodFirstTimeRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                Timber.d("$request")
                val result = SmartPayApi.smartPayApiService.addPaymentAccountAsync(request).await()
                _showDialogLoader.value = false
                Timber.d("Message: ${result.message} status: ${result.status}")
                if (result.status == "0") {
                    registrationDone()
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

    private suspend fun registrationDone() {
        withContext(Dispatchers.Main) {
            try {
                val preferencesEditor = SmartPayApp.preferences.edit()
                preferencesEditor.remove("isAccountDone")
                preferencesEditor.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun navigateToHomeDone(){
        _navigateToHome.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}