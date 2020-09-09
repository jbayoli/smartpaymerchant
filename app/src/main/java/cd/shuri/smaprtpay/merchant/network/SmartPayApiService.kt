package cd.shuri.smaprtpay.merchant.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface SmartPayApiService {
    @POST("register/merchant/api/step1")
    fun sendCodeAsync(@Body request: CodeRequest): Deferred<CommonResponse>

    @POST("register/merchant/api/step2")
    fun validateCodeAsync(@Body request: ValidateCodeRequest) : Deferred<ValidateCodeResponse>

    @POST("register/merchant/api/step3")
    fun signUpAsync(@Body request: SingUpRequest) : Deferred<SingUpResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest) : Response<Unit>

    @POST("register/merchant/api/step4")
    fun registerAsync(
        @Body request: RegisterRequest) : Deferred<CommonResponse>

    @POST("register/merchant/api/step5")
    fun addPaymentAccountAsync(
        @Body request: AddPaymentMethodFirstTimeRequest
    ): Deferred<CommonResponse>

    @POST("api/merchant/compte/add")
    fun addPaymentMethodAsync(
        @Header("Authorization") authorization: String,
        @Body request: AddPaymentMethodRequest
    ): Deferred<CommonResponse>

    @GET("api/default/providers/{type}")
    fun getMobileProvidersAsync(
        @Header("Authorization") authorization: String,
        @Path("type") type : String
    ): Deferred<List<ProvidersData>>

    @GET("api/merchant/compte/all/{customer}")
    fun getPaymentMethodsAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer : String
    ) : Deferred<List<AccountsResponse>>

    @GET("api/merchant/transaction/dashboard/{customer}")
    fun getDashBoardDataAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer : String
    ) : Deferred<DashboardResponse>


    @GET("api/merchant/transaction/waiting/{customer}")
    fun getAllTransactionAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer : String
    ) : Deferred<List<TransactionResponse>>

    @GET("api/merchant/transaction/clos/{customer}")
    fun getAllTransactionDoneAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer : String
    ) : Deferred<List<TransactionResponse>>

    @GET("api/merchant/transaction/all/{customer}")
    fun getWaitingTransactionAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer : String
    ) : Deferred<List<TransactionResponse>>

    @POST("api/merchant/transaction/validate")
    fun validateTransactionAsync(
        @Header("Authorization") authorization: String,
        @Body request: TransactionValidationRequest
    ) : Deferred<CommonResponse>

    @GET("api/default/sectors")
    fun getSectorsAsync(
        @Header("Authorization") authorization: String
    ) : Deferred<List<SectorsResponse>>

    @POST("api/merchant/transfert/new")
    fun cardToEMoneyAsync(
        @Header("Authorization") authorization: String,
        @Body request: TransferRequest
    ): Deferred<TransferResponse>

    @GET("api/merchant/transfert/list/{customer}")
    fun getTransfersAsync(
        @Path("customer") customer: String,
        @Header("Authorization") authorization: String
    ): Deferred<TransferListResponse>

    @POST("api/merchant/password/reinit")
    fun editPasswordAsync(
        @Header("Authorization") authorization: String,
        @Body request: EditPasswordRequestData
    ): Deferred<EditPasswordResponseData>

    @POST("register/merchant/api/reinitPassword/step3")
    fun forgottenPINAsync(
        @Body request: ForgottenPINRequestThree
    ) : Deferred<ForgottenPINResponse>

    @POST("register/merchant/api/reinitPassword/{step}")
    fun forgottenPINAsync(
        @Path("step") step: String,
        @Body request: ForgottenPINRequestOneTwo
    ) : Deferred<ForgottenPINResponse>

    @GET("api/merchant/account/infos/{customer}")
    fun getInfoAsync(
        @Path("customer") customer: String,
        @Header("Authorization") authorization: String
    ): Deferred<Profile>

    @POST("api/merchant/account/infos/update")
    fun updateProfileAsync(
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfile
    ) : Deferred<CommonResponse>

    @GET("api/merchant/account/register/delete/{customer}")
    fun deleteUserAsync(
        @Header("Authorization") authorization: String,
        @Path("customer") customer: String
    ) : Deferred<CommonResponse>
}