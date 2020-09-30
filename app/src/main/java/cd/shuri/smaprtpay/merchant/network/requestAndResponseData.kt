package cd.shuri.smaprtpay.merchant.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String,
    val code: String,
    val fcm: String
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

@Parcelize
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "nom")
    val lastName: String,
    @Json(name = "prenom")
    val firstName: String,
    val email: String,
    val phone1: String,
    val phone2: String = "",
    var activity: String = "", // Nom de l'activité
    var sector: String = "",  //Domaine d'activité
    var rccm: String? = null,
    var nif: String? = null,
    @Json(name = "proprietaire")
    var owner: String? =null,
    var customer: String = "",
    val number: String,
    val commune: String,
    val street: String
):Parcelable

@JsonClass(generateAdapter = true)
data class AddPaymentMethodRequest(
    val operator: String,
    val type: Int,
    val phone: String? = null,
    val card: String? = null,
    val expiration: String? = null,
    val customer: String,
    val shortCode: String,
    val cardName: String? = null,
    val code: String? = null,
    val isMerchant: Boolean
)

@JsonClass(generateAdapter = true)
data class AddPaymentMethodFirstTimeRequest(
    val operator1: String? = null,
    val operator2: String? = null,
    val type1: Int = 0,
    val type2: Int = 0,
    val phone: String? = null,
    val card: String? = null,
    val expiration: String? = null,
    val customer: String,
    val shortCode: String,
    val cardName: String? = null,
    val isMerchant: Boolean
)

@JsonClass(generateAdapter = true)
data class ProvidersData(
    val id: String?,
    val code: String?,
    val name: String?,
    val type: String?
)

@Parcelize
@JsonClass(generateAdapter = true)
data class AccountsResponse(
    val code: String?,
    val card: String?,
    val cardName: String?,
    val expiration: String?,
    val phone: String?,
    val shortCode: String?,
    val operator: String?,
    val operatorName: String?,
    val type: Int?,
    val default: Boolean?
): Parcelable

@JsonClass(generateAdapter = true)
data class DashboardResponse(
    val all: Int? = 0,
    val waiting: Int? = 0,
    val clos: Int? = 0,
    val validatings: Int? = 0,
    val errors: Int? = 0,
    val transferts: Int? = 0,
    val status: String?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class TransactionResponse(
    val date: String?,
    val amount: String?,
    val client: String?,
    val reference: String?,
    val code: String?,
    val paid: Boolean?,
    val step: Int?
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

@JsonClass(generateAdapter = true)
data class TransferRequest(
    val customer: String?,
    val mobile: String?,
    val card: String?,
    val currency: String?,
    val cvv: String?,
    val amount: String?,
    val fcm: String?
)

@JsonClass(generateAdapter = true)
data class TransferResponse(
    val status: String?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class TransferListResponse(
    val status: String?,
    val message: String?,
    val transactions: List<Transfers>
)

@JsonClass(generateAdapter = true)
data class Transfers(
    val code : String?,
    val date: String?,
    val card: String?,
    val mobile: String?,
    val amount: String?,
    val currency: String?,
    val step: Int?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class EditPasswordRequestData(
    val oldPassword: String,
    val newPassword: String,
    val customer: String
)

@JsonClass(generateAdapter = true)
data class EditPasswordResponseData(
    val message: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class ForgottenPINRequestThree(
    val user: String?,
    val password: String?,
    val passwordConfirm: String?
)

@JsonClass(generateAdapter = true)
data class ForgottenPINResponse(
    val message: String?,
    val status: String?,
    val user: String?
)

@JsonClass(generateAdapter = true)
data class ForgottenPINRequestOneTwo(
    val code: String?
)

@JsonClass(generateAdapter = true)
@Parcelize
data class Profile(
    val status: String?,
    val message: String?,
    val username: String?,
    val tokenPhone: String?,
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    @Json(name = "phone1")
    val phone: String?,
    val phone2: String?,
    val adress:String?,
    val activity: String?,
    val rccm: String?,
    val nif: String?,
    val ownerId: String?,
    val sector: String?,
    val created: String?
): Parcelable

@JsonClass(generateAdapter = true)
data class UpdateProfile(
    val customer: String,
    val tokenPhone: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone1: String,
    val phone2: String,
    val adress: String,
    val activity: String,
    val rccm: String,
    val nif: String,
    val ownerId: String,
    val sector: String
)

@JsonClass(generateAdapter = true)
data class DeletePaymentAccount(
    val code: String?,
    val customer: String?
)

@JsonClass(generateAdapter = true)
data class NotificationResponse(
    val message: String?,
    val status: String?,
    val notifications: List<Notification>?
)

@JsonClass(generateAdapter = true)
data class Notification(
    val reference: String?,
    val amount: String?,
    val description: String?,
    val date: String?,
    val customerName: String?,
    val merchantName: String?
)

@JsonClass(generateAdapter = true)
data class Commune(
    val code: String?,
    val name: String?
)