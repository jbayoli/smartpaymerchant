package cd.shuri.smaprtpay.merchant.screens.signupfirst

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpFirstViewModel: ViewModel() {
    private val _isLastNameEmpty= MutableLiveData<Boolean>()
    val isLastNameEmpty : LiveData<Boolean> get() = _isLastNameEmpty

    private val _isFirstNameEmpty= MutableLiveData<Boolean>()
    val isFirstNameEmpty : LiveData<Boolean> get() = _isFirstNameEmpty

    private val _isEmailEmpty= MutableLiveData<Boolean>()
    val isEmailEmpty : LiveData<Boolean> get() = _isEmailEmpty

    private val _isPhoneNumber1Empty= MutableLiveData<Boolean>()
    val isPhoneNumber1Empty : LiveData<Boolean> get() = _isPhoneNumber1Empty

    private val _isPhoneNumber2Empty= MutableLiveData<Boolean>()
    val isPhoneNumber2Empty : LiveData<Boolean> get() = _isPhoneNumber2Empty

    private val _isPhoneNumber1Valid= MutableLiveData<Boolean>()
    val isPhoneNumber1Valid : LiveData<Boolean> get() = _isPhoneNumber1Valid

    private val _isPhoneNumber2Valid= MutableLiveData<Boolean>()
    val isPhoneNumber2Valid : LiveData<Boolean> get() = _isPhoneNumber2Valid

    private val _isAddressEmpty= MutableLiveData<Boolean>()
    val isAddressEmpty : LiveData<Boolean> get() = _isAddressEmpty

    private val _isEmailValid= MutableLiveData<Boolean>()
    val isEmailValid : LiveData<Boolean> get() = _isEmailValid

    private val _navigateToSignUp2= MutableLiveData<Boolean>()
    val navigateToSignUp2 : LiveData<Boolean> get() = _navigateToSignUp2

    fun validateFrom(lastName: String, firstName: String, email: String, phone1: String, phone2: String, address: String)
            :Boolean
    {
        var valid = true

        if (lastName.isEmpty()) {
            _isLastNameEmpty.value = true
            valid = false
        } else {
            _isLastNameEmpty.value = false
        }

        if (firstName.isEmpty()) {
            _isFirstNameEmpty.value = true
            valid = false
        } else {
            _isFirstNameEmpty.value = false
        }

        if (email.isEmpty()) {
            _isEmailEmpty.value = true
            valid = false
        } else {
            _isEmailEmpty.value = false
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _isEmailValid.value = true
            } else {
                _isEmailValid.value = false
                valid = false
            }
        }

        if (phone1.isEmpty()) {
            _isPhoneNumber1Empty.value = true
            valid = false
        } else {
            _isPhoneNumber1Empty.value = false
            if (phone1.length in 1..8) {
                _isPhoneNumber1Valid.value = false
                valid = false
            } else {
                _isPhoneNumber1Valid.value = true
            }
        }

        if (phone2.isEmpty()) {
            _isPhoneNumber2Empty.value = true
            valid = false
        } else {
            _isPhoneNumber2Empty.value = false
            if (phone2.length in 1..8) {
                _isPhoneNumber2Valid.value = false
                valid = false
            } else {
                _isPhoneNumber2Valid.value = true
            }
        }

        if (address.isEmpty()) {
            _isAddressEmpty.value = true
            valid = false
        } else {
            _isAddressEmpty.value = false
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