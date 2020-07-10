package cd.shuri.smaprtpay.merchant.screens.validation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ValidationViewModelFactory (private val phone: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ValidationViewModel::class.java)) {
            return ValidationViewModel(phone) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}