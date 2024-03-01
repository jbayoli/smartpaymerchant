package cd.infoset.smaprtpay.merchant.screens.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestPaymentViewModel @Inject constructor(): ViewModel() {
    var requestPaymentData by mutableStateOf(RequestPaymentData())
}