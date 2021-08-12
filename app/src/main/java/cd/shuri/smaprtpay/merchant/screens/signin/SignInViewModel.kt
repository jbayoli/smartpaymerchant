package cd.shuri.smaprtpay.merchant.screens.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.CommonResponse
import cd.shuri.smaprtpay.merchant.network.LoginRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.ConnectException

class SignInViewModel : ViewModel() {
    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val  showDialogLoader : LiveData<Boolean?> get() = _showDialogLoader

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome : LiveData<Boolean?> get() = _navigateToHome

    private val _isPasswordEmpty = MutableLiveData<Boolean>()
    val isPasswordEmpty : LiveData<Boolean> get() = _isPasswordEmpty

    private val _isUserNameEmpty = MutableLiveData<Boolean>()
    val isUserNameEmpty : LiveData<Boolean> get() = _isUserNameEmpty

    private val _loginStatus = MutableLiveData<Int?>()
    val loginStatus : LiveData<Int?> get() = _loginStatus

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private val _step = MutableLiveData<Int?>()
    val step : LiveData<Int?> get() = _step

    private val _response = MutableLiveData<CommonResponse>()
    val response: LiveData<CommonResponse> get() = _response


    var token = ""

    private var auth: String? = null
    private var customer: String? = null
    private var roles: String? = null
    private var name: String? = null

    init {

        _isUserNameEmpty.value = false
        _isPasswordEmpty.value = false

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
                if (response.status == HttpStatusCode.OK) {
                    auth = response.headers[HttpHeaders.Authorization]
                    customer = response.headers["customer"]
                    roles = response.headers["roles"]
                    name = response.headers["name"]
                    val phone = response.headers["phone"]
                    val savedStep = response.headers["step"]?.toInt()
                    Timber.d("user code = $customer")
                    Timber.d("auth = $auth")
                    Timber.d("roles = $roles")
                    Timber.d("name = $name")
                    _showDialogLoader.value = false
                    if (roles == "MERCHANT") {
                        _loginStatus.value = 0
                        savePreference(customer!!, auth!!, name!!, savedStep!!, request.username, phone!!)
                        _step.value = savedStep
                        Timber.d("step == ${_step.value}")
                    } else {
                        _loginStatus.value = 1
                    }
                }
            } catch (e: Exception) {
                Timber.e("$e")
                if (e is ClientRequestException) {
                    _showDialogLoader.value = false
                    _loginStatus.value = 1
                }
                if (e is ConnectException) {
                    _showDialogLoader.value = false
                    _showTToastForError.value = true
                }
            }
        }
    }

    fun  deleteUser() {
        viewModelScope.launch {
            try {
                val userCode = SmartPayApp.preferences.getString("user_code", "")
                val userToken = SmartPayApp.preferences.getString("token", "")
                val auth = "Bearer $userToken"
                _showDialogLoader.value = true
                val result = SmartPayApi.smartPayApiService.deleteUserAsync(auth, userCode!!)
                Timber.d("$result")
                _response.value = result
                _showDialogLoader.value = false
            } catch (e: Exception) {
                _showDialogLoader.value = false
                _showTToastForError.value = true
                Timber.e("$e")
            }
        }
    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastErrorDone() {
        _showTToastForError.value = null
    }

    fun signIn() {
        _navigateToHome.value = true
    }

    private suspend fun savePreference(userCode: String, auth: String, name: String, step: Int, userName: String, defaultPhone:String) {
        withContext(Dispatchers.Main) {
            val preferencesEditor = SmartPayApp.preferences.edit()
            preferencesEditor.putString("fcm", token)
            preferencesEditor.putString("user_code", userCode)
            preferencesEditor.putString("token", auth)
            preferencesEditor.putString("name", name)
            preferencesEditor.putString("username", userName)
            preferencesEditor.putInt("step", step)
            preferencesEditor.putString("default_phone", defaultPhone.replaceFirst("243", ""))
            preferencesEditor.apply()
        }
    }


    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }

    fun showToastDone() {
        _loginStatus.value = null
    }
}