package cd.shuri.smaprtpay.merchant.screens.ticket

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.util.concurrent.ExecutionException

class SearchTicketViewModel(application: Application) : AndroidViewModel(application) {
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null
    private val _isOpen = MutableLiveData<Boolean>()
    val isOpen: LiveData<Boolean> get() = _isOpen
    private val _ticketScanned = MutableLiveData<String>()
    val ticketScanned : LiveData<String> get() = _ticketScanned

    val processCameraProvider: LiveData<ProcessCameraProvider>
    get() {
        if (cameraProviderLiveData == null) {
            cameraProviderLiveData = MutableLiveData()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())
            cameraProviderFuture.addListener(
                {
                    try {
                        cameraProviderLiveData!!.value = cameraProviderFuture.get()
                    } catch (e: ExecutionException) {
                        Timber.e("ExecutionException $e")
                    } catch (e: InterruptedException) {
                        Timber.e("InterruptedException $e")
                    }
                },
                ContextCompat.getMainExecutor(getApplication())
            )
        }
        return cameraProviderLiveData!!
    }

    fun reinitOpen() {
        _isOpen.value = null
        _ticketScanned.value = null
    }

    fun setOpen(ticketScanned: String) {
        if (_isOpen.value == null)  {
            _isOpen.value = true
            _ticketScanned.value = ticketScanned
        }
    }
}