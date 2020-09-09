package cd.shuri.smaprtpay.merchant.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SmartPayApi {
    //Base api url
    private const val BASE_URL = "http://41.243.7.46:3005/"

    //Set connection timeout, write timeout and read timeout
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
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