package cd.shuri.smaprtpay.merchant

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

class SmartPayApp :  Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        preferences =  applicationContext.getSharedPreferences("cd.infoset.smartpay.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        userToken = preferences.getString("token", "")
        auth = "Bearer $userToken"
        context = applicationContext
    }

    companion object {
        lateinit var preferences : SharedPreferences
        lateinit var context: Context
        val dialogLoader = LoaderDialog()
        var userToken : String? = null
        lateinit var auth : String
    }
}