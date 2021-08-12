package cd.shuri.smaprtpay.merchant.network

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

object SmartPayApi {
    //Base api url
    private const val HOST = "41.243.7.46"
    private const val PORT = 3006

    private val HTTP_CLIENT = HttpClient{
        install(JsonFeature){
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    //Instantiate the service so that we can access APIs in this object
    val smartPayApiService = SmartPayApiService(HOST, PORT, HTTP_CLIENT)

    fun closeApi() {
        HTTP_CLIENT.close()
    }
}