package cd.shuri.smaprtpay.merchant.screens.splashscreen

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashScreenViewModel: ViewModel() {
    private val _navigateTo = MutableLiveData<Boolean?>()
    val navigateTo : LiveData<Boolean?> get() = _navigateTo


    private val _currentTime = MutableLiveData<Long>()

    private var timer : CountDownTimer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
        override fun onFinish() {
            _currentTime.value = DONE
            _navigateTo.value = true
        }

        override fun onTick(millisUntilFinished: Long) {
            _currentTime.value = millisUntilFinished/ONE_SECOND
        }
    }


    init {

        timer.start()
    }

    fun navigateToDone() {
        _navigateTo.value = null
    }

    companion object {
        //Timer when the game is over
        private const val DONE = 0L
        //Countdown time interval
        private const val ONE_SECOND = 1000L
        //Total time for the game
        private const val COUNTDOWN_TIME = 3000L
    }
}