package cd.shuri.smaprtpay.merchant

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

class SmartPayApp :  Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        preferences =  applicationContext.getSharedPreferences("cd.infoset.smartpay.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    }

    companion object {
        lateinit var preferences : SharedPreferences
    }

}