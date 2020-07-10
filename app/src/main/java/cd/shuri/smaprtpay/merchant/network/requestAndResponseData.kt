package cd.shuri.smaprtpay.merchant.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String,
    val code: String
)

@JsonClass(generateAdapter = true)
data class CommonResponse(
    val message: String,
    val status: String
)

@JsonClass(generateAdapter = true)
data class CodeRequest(
    val phone: String,
    val fcm: String
)

@JsonClass(generateAdapter = true)
data class ValidateCodeRequest(
    val phone: String,
    val fcm: String,
    val code: String
)

@JsonClass(generateAdapter = true)
data class ValidateCodeResponse(
    val message: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class SingUpRequest(
    val code: String,
    val username: String,
    val password: String,
    val confirmedPassword: String,
    val phone: String,
    val fcm: String
)

@JsonClass(generateAdapter = true)
data class SingUpResponse(
    val message: String?,
    val customer: String?,
    val token: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "nom")
    val lastName: String,
    @Json(name = "prenom")
    val firstName: String,
    val email: String,
    val phone1: String,
    val phone2: String,
    @Json(name = "adress")
    val address: String,
    val activity: String,
    val sector: String,
    val rccm: String,
    val nif: String,
    @Json(name = "proprietaire")
    val owner: String,
    val customer: String
)

@JsonClass(generateAdapter = true)
data class AddPaymentMethodRequest(
    val operator: String,
    val type: Int,
    val phone: String? = null,
    val card: String? = null,
    val expiration: String? = null,
    val customer: String,
    val shortCode: String
)

@JsonClass(generateAdapter = true)
data class ProvidersData(
    val id: String?,
    val code: String?,
    val name: String?,
    val type: String?
)


@JsonClass(generateAdapter = true)
data class AccountsResponse(
    val code: String?,
    val phone: String?,
    val value: String?,
    val type: String?,
    val shortCode: String?
)

@JsonClass(generateAdapter = true)
data class DashboardResponse(
    val all: Int?,
    val waiting: Int?,
    val clos: Int?,
    val status: String?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class TransactionResponse(
    val date: String?,
    val amount: String?,
    val client: String?,
    val reference: String?,
    val code: String?
)

@JsonClass(generateAdapter = true)
data class TransactionValidationRequest(
    val transaction: String,
    val customer: String,
    val validated: Boolean,
    val fcm: String
)

@JsonClass(generateAdapter = true)
data class SectorsResponse(
    val id: Int?,
    val code: String?,
    val name: String?
)