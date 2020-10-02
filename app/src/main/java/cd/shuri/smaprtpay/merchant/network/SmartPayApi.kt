package cd.shuri.smaprtpay.merchant.network

import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SmartPayApi {
    //Base api url
    private val host = SmartPayApp.context.getString(R.string.host_test)
    private val BASE_URL = host

    //Set connection timeout, write timeout and read timeout
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    //instantiate retrofit with timeouts, Moshi Converter Factory, Moshi coroutine factory and base url
    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

    //Instantiate the service so that we can access APIs in this object
    val smartPayApiService: SmartPayApiService = retrofit.create(SmartPayApiService::class.java)
}