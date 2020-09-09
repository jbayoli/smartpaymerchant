package cd.shuri.smaprtpay.merchant.screens.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.network.CommonResponse
import cd.shuri.smaprtpay.merchant.network.LoginRequest
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.*
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

    private val _showTToastForError = MutableLiveData<Boolean>()
    val showTToastForError: LiveData<Boolean> get() = _showTToastForError

    private val _step = MutableLiveData<Int>()
    val step : LiveData<Int> get() = _step

    private val _response = MutableLiveData<CommonResponse>()
    val response: LiveData<CommonResponse> get() = _response


    var token = ""

    private var auth: String? = null
    private var customer: String? = null
    private var roles: String? = null
    private var name: String? = null

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
                    auth = response.headers().get("Authorization")
                    customer = response.headers().get("customer")
                    roles = response.headers().get("roles")
                    name = response.headers().get("name")
                    val savedStep = response.headers().get("step")?.toInt()
                    Timber.d("user code = $customer")
                    Timber.d("auth = $auth")
                    Timber.d("roles = $roles")
                    Timber.d("name = $name")
                    _showDialogLoader.value = false
                    if (roles == "MERCHANT") {
                        _loginStatus.value = 0
                        savePreference(customer!!, auth!!, name!!, savedStep!!, request.username)
                        _step.value = savedStep
                        Timber.d("step == ${_step.value}")
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
                _showTToastForError.value = true
                Timber.e("$e")
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
                val result = SmartPayApi.smartPayApiService.deleteUserAsync(auth, userCode!!).await()
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

    private suspend fun savePreference(userCode: String, auth: String, name: String, step: Int, userName: String) {
        withContext(Dispatchers.Main) {
            val preferencesEditor = SmartPayApp.preferences.edit()
            preferencesEditor.putString("fcm", token)
            preferencesEditor.putString("user_code", userCode)
            preferencesEditor.putString("token", auth)
            preferencesEditor.putString("name", name)
            preferencesEditor.putString("username", userName)
            preferencesEditor.putInt("step", step)
            preferencesEditor.apply()
        }
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