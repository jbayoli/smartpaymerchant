package cd.shuri.smaprtpay.merchant.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object RegisterStep {
    const val StepOne = "step1"
    const val StepTwo = "step2"
    const val StepFour = "step4"
    const val StepFive = "step5"
}

object TransactionType {
    const val All = "all"
    const val Unsuccessful = "unsuccessfull"
    const val Pending = "waiting"
    const val ToValidate = "validating"
}

object ProviderType {
    const val Mobile = 1
    const val Card = 2
}

object PaymentMethodAction {
    const val Add = "add"
    const val Edit = "edit"
    const val Delete = "delete"
}

object ForgottenPinStep {
    const val StepOne = "step1"
    const val StepTwo = "step2"
    const val StepTree = "step3"
}

class SmartPayApiService(
    private val host: String,
    private val port: Int,
    private val httpClient: HttpClient
) {

    private val baseUrl = "https://backend.flexpay.cd"

    /**
     * Combined the request of step1, step2, step4, step5 in registration process
     * @param [step] is one of the value in [RegisterStep]
     * @param [request] is on of the registration request except [SingUpRequest]
     * @return [CommonResponse]
     */
    suspend fun combinedRegisterRequest(
        step: String,
        request: Any
    ): CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/register/merchant/api/$step"
        ) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    /**
     * Sign up the user to FlexPay System.
     * @param [request] the request to send.
     * @return [SingUpResponse] the response of the server
     */
    suspend fun signUpAsync(request : SingUpRequest): SingUpResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/register/merchant/api/step3"
        ) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    /**
     * Connects the user in FlexPay.
     * @param [request] the login request
     * @return [HttpResponse]
     */
    suspend fun login(request: LoginRequest): HttpResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/login"
        ) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    /**
     * Perform a payment method request from the given action
     * @param [authorization] Authorisation need for authenticate the request
     * @param [action] the action to make in the request, which is one of the [PaymentMethodAction]
     * @param [request] the request to be sent
     * @return [CommonResponse]
     */
    suspend fun addEditOrDeletePaymentMethodAsync(
        authorization: String,
        action: String,
        request: Any
    ): CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/api/merchant/compte/$action"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }

    /**
     * Fetch all providers that match the given type
     * @param [authorization] Authorisation need for authenticate the request
     * @param [type] One of the type in [ProviderType]
     * @return [ProvidersData]s
     */
    suspend fun getProvidersAsync(
        authorization: String,
        type: Int
    ) : List<ProvidersData> {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/default/providers/$type"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Fetch all payment method that match the given customer
     * @param [authorization] the token to authenticate the request
     * @param [customer] from which one we fetch all the payment method
     * @return [AccountsResponse]s
     */
    suspend fun getPaymentMethodsAsync(
        authorization: String,
        customer: String
    ): List<AccountsResponse> {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/compte/all/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Fetch all the dashboard data from the given customer.
     * @param [authorization] token to authenticate the request
     * @param [customer] from which one we fetch the data
     * @return [DashboardResponse]
     */
    suspend fun getDashBoardDataAsync(
        authorization: String,
        customer: String
    ): DashboardResponse {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/transaction/dashboard/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Fetch transactions from the given type and customer.
     * @param authorization token to authenticate the request
     * @param type of transaction to fetch
     * @param customer from which we fetch transactions
     * @return [TransactionResponse]s
     */
    suspend fun getTransactionByTypeAsync(
        authorization: String,
        type: String,
        customer: String
    ): List<TransactionResponse> {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/transaction/$type/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Validate or cancel a transaction
     * @param authorization token to authenticate the request
     * @param request to validate or cancel a transaction
     * @return [CommonResponse]
     */
    suspend fun validateTransactionAsync(
        authorization: String,
        request: TransactionValidationRequest
    ): CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/api/merchant/transaction/validate"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }

    /**
     * Fetch sectors
     * @param authorization token to authenticate request
     * @return [SectorsResponse]s
     */
    suspend fun getSectorsAsync(authorization: String): List<SectorsResponse> {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/default/sectors"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Edit the password of the user
     * @param authorization token to authenticate the request
     * @param request to edit the password
     * @return [CommonResponse]
     */
    suspend fun editPasswordAsync(
        authorization: String,
        request: EditPasswordRequestData
    ): CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/api/merchant/password/reinit"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }

    /**
     * Perform a password(PIN) reset request.
     * @param step one of the [ForgottenPinStep]
     * @param request to be sent
     * @return [ForgottenPINResponse]
     */
    suspend fun forgottenPINAsync(
        step: String,
        request: Any
    ): ForgottenPINResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/register/merchant/api/reinitPassword/$step"
        ) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    /**
     * Fetch the user information from the given customer
     * @param customer from whom we fetch the data
     * @param authorization token to authenticate the request sender
     * @return [Profile]
     */
    suspend fun getInfoAsync(
        customer: String,
        authorization: String
    ): Profile {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/account/infos/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Update the user information
     * @param authorization token to authenticate the request sender
     * @param request to update the user information
     * @return [CommonResponse]
     */
    suspend fun updateProfileAsync(
        authorization: String,
        request: UpdateProfile
    ) : CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/api/merchant/account/infos/update"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }

    /**
     * Delete the user
     * @param authorization token to authenticate the request sender
     * @param customer who will be deleted
     * @return [CommonResponse]
     */
    suspend fun deleteUserAsync(
        authorization: String,
        customer: String
    ): CommonResponse {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/account/register/delete/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Fetch notification
     * @param authorization token to authenticate request sender
     * @param customer from whom we fetch notifications
     * @return [NotificationResponse]
     */
    suspend fun getNotificationsAsync(
        authorization: String,
        customer: String
    ): NotificationResponse {
        return  httpClient.get(
            host = host,
            port = port,
            path = "/api/merchant/notification/list/$customer"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
        }
    }

    /**
     * Fetch communes
     * @return [Commune]s
     */
    suspend fun getCommuneAsync(): List<Commune> = httpClient.get(
        host = host,
        port = port,
        path = "/api/support/communes"
    ) {
        contentType(ContentType.Application.Json)
    }

    /**
     * Check the validity of a ticket
     * @param authorization token to authenticate the request sender
     * @param request to validate the ticket
     * @return [TicketVerificationResult]
     */
    suspend fun verifyTicketAsync(
        authorization: String,
        request: TicketVerification
    ): TicketVerificationResult {
        return httpClient.post(
            host = host,
            port = port,
            path = "api/merchant/ticket/verify"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }

    /**
     * Get all help data need
     */
    suspend fun getHelpDataAsync(): HelpData {
        return httpClient.get(
            host = host,
            port = port,
            path = "/api/support/help"
        ) {
            contentType(ContentType.Application.Json)
        }
    }

    /**
     *Initiate payment from the merchant
     * @param authorization token to authenticate the request sender
     * @param request to be sent to initiate a payment
     * @return [CommonResponse]
     */
    suspend fun paymentAsync(
        authorization: String,
        request: Payment
    ): CommonResponse {
        return httpClient.post(
            host = host,
            port = port,
            path = "/api/merchant/receive/payment"
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, authorization)
            body = request
        }
    }
}