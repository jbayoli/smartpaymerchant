package cd.shuri.smaprtpay.merchant.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SmartPayApi {
    //Base api url
    private const val HOST = "41.243.7.46"
    private const val PORT = 3006

    private val HTTP_CLIENT = HttpClient{
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }
    }

    //Instantiate the service so that we can access APIs in this object
    val smartPayApiService = SmartPayApiService(HOST, PORT, HTTP_CLIENT)

    fun closeApi() {
        HTTP_CLIENT.close()
    }
}