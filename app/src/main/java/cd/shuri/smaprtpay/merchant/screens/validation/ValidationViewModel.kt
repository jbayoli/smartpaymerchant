package cd.shuri.smaprtpay.merchant.screens.validation

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.network.CodeRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import cd.shuri.smaprtpay.merchant.network.ValidateCodeRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ValidationViewModel (phone: String): ViewModel(){
    private val _navigateToSignUpFragment = MutableLiveData<Boolean?> ()
    val navigateToSignUpFragment : LiveData<Boolean?> get() = _navigateToSignUpFragment

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime : LiveData<Long> get() = _currentTime

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    lateinit var timer : CountDownTimer

    //The String version of the current time
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val _isTimerEnable = MutableLiveData<Boolean> ()
    val isTimerEnabled : LiveData<Boolean> get() = _isTimerEnable

    private val _isCodeCorrect = MutableLiveData<Boolean>()
    val isCodeCorrect : LiveData<Boolean> get() = _isCodeCorrect

    private val _isResendEnable = MutableLiveData<Boolean>()
    val isResendEnable : LiveData<Boolean> get() = _isResendEnable

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var token = ""
    private var phoneNumber = ""

    init {
        phoneNumber = phone
        _isTimerEnable.value = false
        _isCodeCorrect.value = true
        _isResendEnable.value = false
        getCode()
    }

    private fun getCode() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e("getInstanceId failed ${task.exception}")
                return@addOnCompleteListener
            }
            //Get new Instance ID token
            token = task.result ?: ""
            // Log and toast
            Timber.d("token : $token")
        }
    }
    fun sendingCodeTimer() {
        //Creates a timer which triggers the end of the game when it finishes
        _isTimerEnable.value = true
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onFinish() {
                _currentTime.value = DONE
                _isResendEnable.value = true
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ONE_SECOND
            }
        }
        timer.start()
    }

    private fun sendCode(request: CodeRequest) {
        viewModelScope.launch {
            try {
                val result = SmartPayApi.smartPayApiService.sendCodeAsync(request).await()
                Timber.d("message: ${result.message} status: ${result.status}")
            } catch (e: Exception) {
                Timber.e("$e")
            }
        }
    }

    fun resendCode(request: CodeRequest) {
        _isResendEnable.value = false
        Timber.d("Resend phone: ${request.phone} fcm: ${request.fcm}")
        sendingCodeTimer()
        sendCode(request)
    }

    private fun validateCode(code: String){
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.validateCodeAsync(
                    ValidateCodeRequest(phoneNumber, token, code)
                ).await()
                Timber.d("message: ${result.message} status: ${result.status}")

                if (result.status == "0") {
                    _navigateToSignUpFragment.value = true
                    _showDialogLoader.value = false
                } else {
                    _isCodeCorrect.value = false
                    _showDialogLoader.value = false
                }

            } catch (e: Exception) {
                Timber.e("$e")
                _showDialogLoader.value = false
                _showTToastForError.value = true
            }
        }
    }

    fun navigateToSignUpFragmentDone() {
        _navigateToSignUpFragment.value = null
    }

    fun checkCodeField(code: String){
        _isCodeCorrect.value = code.length == 6
        if (_isCodeCorrect.value!!) validateCode(code)
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    companion object {
        //Timer when the game is over
        private const val DONE = 0L
        //Countdown time interval
        private const val ONE_SECOND = 1000L
        //Total time for the game
        private const val COUNTDOWN_TIME = 1_800_000L
    }
}