package cd.shuri.smaprtpay.merchant.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.network.CodeRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountViewModel: ViewModel() {

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _isPhoneNumberCorrect = MutableLiveData<Boolean>()
    val isPhoneNumberCorrect : LiveData<Boolean> get() = _isPhoneNumberCorrect

    private val _navigateToValidationFragment  = MutableLiveData<Boolean>()
    val navigateToValidationFragment : LiveData<Boolean> get() = _navigateToValidationFragment

    private val _showDialogLoader = MutableLiveData<Boolean>()
    val showDialogLoader: LiveData<Boolean> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private var token = ""

    private var countryCode = ""

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _isPhoneNumberCorrect.value = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.e("getInstanceId failed ${task.exception}")
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                token = task.result?.token!!
                // Log and toast
                Timber.d("token : $token")
            })
    }

    fun setPhoneNumber(phoneNumber : String) {
        _phoneNumber.value = phoneNumber
        checkPhoneNumberField()
    }

    private fun sendCode(phoneNumber: String) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val request = CodeRequest(phoneNumber, token)
                val result = SmartPayApi.smartPayApiService.sendCodeAsync(request).await()
                Timber.d("message: ${result.message} status: ${result.status}")
                _showDialogLoader.value = false
                _navigateToValidationFragment.value = true
            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
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
                sendCode("$countryCode${phoneNumber.value}")
            }
        }
    }

    fun navigateToValidationFragmentDone() {
        _navigateToValidationFragment.value = null
    }
}