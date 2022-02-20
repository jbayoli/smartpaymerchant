package cd.shuri.smaprtpay.merchant.screens.editpaymentaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditPaymentAccountViewModelFactory(private val account: Int): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPaymentAccountViewModel::class.java)) {
            return EditPaymentAccountViewModel(account) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}