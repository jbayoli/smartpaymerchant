package cd.shuri.smaprtpay.merchant.screens.signupfirst

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.network.Commune
import cd.shuri.smaprtpay.merchant.network.RegisterRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

class SignUpFirstViewModel: ViewModel() {
    private val _isLastNameEmpty= MutableLiveData<Boolean>()
    val isLastNameEmpty : LiveData<Boolean> get() = _isLastNameEmpty

    private val _isFirstNameEmpty= MutableLiveData<Boolean>()
    val isFirstNameEmpty : LiveData<Boolean> get() = _isFirstNameEmpty

    private val _isEmailEmpty= MutableLiveData<Boolean>()
    val isEmailEmpty : LiveData<Boolean> get() = _isEmailEmpty

    private val _isPhoneNumber1Empty= MutableLiveData<Boolean>()
    val isPhoneNumber1Empty : LiveData<Boolean> get() = _isPhoneNumber1Empty

    private val _isPhoneNumber1Valid= MutableLiveData<Boolean>()
    val isPhoneNumber1Valid : LiveData<Boolean> get() = _isPhoneNumber1Valid

    private val _isPhoneNumber2Valid= MutableLiveData<Boolean>()
    val isPhoneNumber2Valid : LiveData<Boolean> get() = _isPhoneNumber2Valid

    private val _isAddressNumberEmpty= MutableLiveData<Boolean>()
    val isAddressNumberEmpty : LiveData<Boolean> get() = _isAddressNumberEmpty

    private val _isAddressStreetEmpty= MutableLiveData<Boolean>()
    val isAddressStreetEmpty : LiveData<Boolean> get() = _isAddressStreetEmpty

    private val _isAddressCommuneEmpty= MutableLiveData<Boolean>()
    val isAddressCommuneEmpty : LiveData<Boolean> get() = _isAddressCommuneEmpty

    private val _isEmailValid= MutableLiveData<Boolean>()
    val isEmailValid : LiveData<Boolean> get() = _isEmailValid

    private val _navigateToSignUp2= MutableLiveData<Boolean?>()
    val navigateToSignUp2 : LiveData<Boolean?> get() = _navigateToSignUp2

    private val _communes = MutableLiveData(listOf<Commune>())
    val communes: LiveData<List<Commune>> get() = _communes

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    init {
        getAllCommunes()
    }

    private fun getAllCommunes() {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.getCommuneAsync()
                _showDialogLoader.value = false
                if (result.isNotEmpty()) {
                    _communes.value = result
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                Timber.e(e)
                if (e is ConnectException) {
                    _showTToastForError.value = true
                }
            }
        }
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    fun validateFrom(request: RegisterRequest)
            :Boolean
    {
        var valid = true

        if (request.lastName.isEmpty()) {
            _isLastNameEmpty.value = true
            valid = false
        } else {
            _isLastNameEmpty.value = false
        }

        if (request.firstName.isEmpty()) {
            _isFirstNameEmpty.value = true
            valid = false
        } else {
            _isFirstNameEmpty.value = false
        }

        if (request.email.isNotEmpty()) {
            if (Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
                _isEmailValid.value = true
            } else {
                _isEmailValid.value = false
                valid = false
            }
        }

        if (request.phone1.isEmpty()) {
            _isPhoneNumber1Empty.value = true
            valid = false
        } else {
            _isPhoneNumber1Empty.value = false
            if (request.phone1.length in 1..8) {
                _isPhoneNumber1Valid.value = false
                valid = false
            } else {
                _isPhoneNumber1Valid.value = true
            }
        }

        if (request.phone2.isNotEmpty()) {
            if (request.phone2.length in 1..8) {
                _isPhoneNumber2Valid.value = false
                valid = false
            } else {
                _isPhoneNumber2Valid.value = true
            }
        }

        if (request.commune.isEmpty()) {
            _isAddressCommuneEmpty.value = true
            valid = false
        } else {
            _isAddressCommuneEmpty.value = false
        }

        if (request.number.isEmpty()) {
            _isAddressNumberEmpty.value = true
            valid = false
        } else {
            _isAddressNumberEmpty.value = false
        }

        if (request.street.isEmpty()) {
            _isAddressStreetEmpty.value = true
            valid = false
        } else {
            _isAddressStreetEmpty.value = false
        }
        return valid
    }

    fun setNavigateToHome(navigate: Boolean) {
        _navigateToSignUp2.value = navigate
    }

    fun navigateToSignUp2Done() {
        _navigateToSignUp2.value = null
    }
}