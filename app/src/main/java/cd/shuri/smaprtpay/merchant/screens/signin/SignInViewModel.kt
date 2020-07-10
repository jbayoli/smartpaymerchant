package cd.shuri.smaprtpay.merchant.screens.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.LoginRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel : ViewModel() {
    private val _showDialogLoader = MutableLiveData<Boolean>()
    val  showDialogLoader : LiveData<Boolean> get() = _showDialogLoader

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome : LiveData<Boolean> get() = _navigateToHome

    private val _isPasswordEmpty = MutableLiveData<Boolean>()
    val isPasswordEmpty : LiveData<Boolean> get() = _isPasswordEmpty

    private val _isUserNameEmpty = MutableLiveData<Boolean>()
    val isUserNameEmpty : LiveData<Boolean> get() = _isUserNameEmpty

    private val _loginStatus = MutableLiveData<Int>()
    val loginStatus : LiveData<Int> get() = _loginStatus

    var token = ""

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        _isUserNameEmpty.value = false
        _isPasswordEmpty.value = false

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

    fun validateForm(request: LoginRequest) : Boolean {
        var valid = true

        if (request.username.isEmpty()) {
            _isUserNameEmpty.value = true
            valid = false
        } else {
            _isUserNameEmpty.value = false
        }

        if (request.password.isEmpty()) {
            _isPasswordEmpty.value = true
            valid = false
        } else {
            _isPasswordEmpty.value = false
        }
        return valid
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            try {
                _showDialogLoader.value = true
                val response = SmartPayApi.smartPayApiService.login(request)
                if (response.isSuccessful) {
                    val auth = response.headers().get("Authorization")
                    val customer = response.headers().get("customer")
                    val roles = response.headers().get("roles")
                    Timber.d("user code = $customer")
                    Timber.d("auth = $auth")
                    Timber.d("roles = $roles")
                    _showDialogLoader.value = false
                    if (roles == "MERCHANT") {
                        _loginStatus.value = 0
                        savePreference(customer!!, auth!!)
                    } else {
                        _loginStatus.value = 1
                    }
                } else {
                    _showDialogLoader.value = false
                    Timber.d("response code = ${response.code()}")
                    if (response.code() == 403 || response.code() == 404) {
                        _loginStatus.value = 1
                    }
                }
            } catch (e: Exception) {
                _showDialogLoader.value = false
                Timber.e("$e")
            }
        }
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    private fun savePreference(userCode: String, auth: String) {
        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("fcm", token)
        preferencesEditor.putString("user_code", userCode)
        preferencesEditor.putString("token", auth)
        preferencesEditor.apply()
        _navigateToHome.value = true
    }

    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    fun showToastDone() {
        _loginStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}