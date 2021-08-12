package cd.shuri.smaprtpay.merchant.screens.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.network.HelpData
import cd.shuri.smaprtpay.merchant.network.SmartPayApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class HelpViewModel : ViewModel() {
    private val _help = MutableLiveData<HelpData>()
    val help : LiveData<HelpData> get() = _help

    private val _showDialogLoader = MutableLiveData<Boolean?>()
    val showDialogLoader: LiveData<Boolean?> get() = _showDialogLoader

    private val _showTToastForError = MutableLiveData<Boolean?>()
    val showTToastForError: LiveData<Boolean?> get() = _showTToastForError

    private var viewModelJob = Job()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        //getHelpData()
    }

//    private fun getHelpData() {
//        viewModelScope.launch {
//            try {
//                _showDialogLoader.value = true
//                val result = SmartPayApi.smartPayApiService.getHelpDataAsync().await()
//                _help.value = result
//                _showDialogLoader.value = false
//            } catch (e: Exception) {
//                Timber.i("$e")
//                _showDialogLoader.value = false
//                _showTToastForError.value = true
//            }
//        }
//    }

    fun showDialogLoaderDone() {
        _showDialogLoader.value = null
    }

    fun showToastErrorDone2() {
        _showTToastForError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}