package cd.shuri.smaprtpay.merchant.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class AccountViewModel: ViewModel() {

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _isPhoneNumberCorrect = MutableLiveData<Boolean>()
    val isPhoneNumberCorrect : LiveData<Boolean> get() = _isPhoneNumberCorrect

    private val _navigateToValidationFragment  = MutableLiveData<Boolean>()
    val navigateToValidationFragment : LiveData<Boolean> get() = _navigateToValidationFragment

    init {
        _isPhoneNumberCorrect.value = true
    }

    fun setPhoneNumber(phoneNumber : String) {
        _phoneNumber.value = phoneNumber
        checkPhoneNumberField()
    }

    private fun checkPhoneNumberField() {
        if (phoneNumber.value?.isEmpty()!!)
            _isPhoneNumberCorrect.value = false
        else {
            if (phoneNumber.value?.length in 1..8)
                _isPhoneNumberCorrect.value = false
            else {
                Timber.d("Ok")
                _isPhoneNumberCorrect.value = true
                _navigateToValidationFragment.value = true
            }
        }
    }

    fun navigateToValidationFragmentDone() {
        _navigateToValidationFragment.value = null
    }
}