package cd.shuri.smaprtpay.merchant.network

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val code: String,
    val fcm: String
)

@Keep
@Serializable
data class CommonResponse(
    val message: String,
    val status: String
)

@Keep
@Serializable
data class CodeRequest(
    val phone: String,
    val fcm: String
)

@Keep
@Serializable
data class ValidateCodeRequest(
    val phone: String,
    val fcm: String,
    val code: String
)

@Keep
@Serializable
data class SingUpRequest(
    val code: String,
    val username: String,
    val password: String,
    val confirmedPassword: String,
    val phone: String,
    val fcm: String
)

@Keep
@Serializable
data class SingUpResponse(
    val message: String?,
    val customer: String?,
    val token: String?,
    val status: String?
)

@Keep
@Serializable
@Parcelize
data class RegisterRequest(
    @SerialName("nom")
    val lastName: String,
    @SerialName( "prenom")
    val firstName: String,
    val email: String,
    val phone1: String,
    val phone2: String = "",
    var activity: String = "", // Nom de l'activité
    var sector: String = "",  //Domaine d'activité
    var rccm: String? = null,
    var nif: String? = null,
    @SerialName("proprietaire")
    var owner: String? =null,
    var customer: String = "",
    val number: String,
    val commune: String,
    val street: String,
    val sex: String
):Parcelable

@Keep
@Serializable
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
    val isMerchant: String
)

@Keep
@Serializable
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
)

@Keep
@Serializable
data class ProvidersData(
    val id: Int?,
    val code: String?,
    val name: String?,
    val type: Int?
)

@Keep
@Serializable
@Parcelize
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

@Keep
@Serializable
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

@Keep
@Serializable
data class TransactionResponse(
    val date: String?,
    val amount: String?,
    val client: String?,
    val reference: String?,
    val code: String?,
    val paid: Boolean?,
    val step: Int?,
    val frais: String?,
    val status: String?
)

@Keep
@Serializable
data class TransactionValidationRequest(
    val transaction: String,
    val customer: String,
    val validated: Boolean,
    val fcm: String
)

@Keep
@Serializable
data class SectorsResponse(
    val code: String?,
    val name: String?
)

@Keep
@Serializable
data class TransferRequest(
    val customer: String?,
    val mobile: String?,
    val card: String?,
    val currency: String?,
    val cvv: String?,
    val amount: String?,
    val fcm: String?
)

@Keep
@Serializable
data class TransferListResponse(
    val status: String?,
    val message: String?,
    val transactions: List<Transfers>
)

@Keep
@Serializable
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

@Keep
@Serializable
data class EditPasswordRequestData(
    val oldPassword: String,
    val newPassword: String,
    val customer: String
)

@Keep
@Serializable
data class ForgottenPINRequestThree(
    val user: String?,
    val password: String?,
    val passwordConfirm: String?
)

@Keep
@Serializable
data class ForgottenPINResponse(
    val message: String?,
    val status: String?,
    val user: String?
)

@Keep
@Serializable
data class ForgottenPINRequestOneTwo(
    val code: String?
)

@Keep
@Serializable
@Parcelize
data class Profile(
    val status: String?,
    val message: String?,
    val username: String?,
    val tokenPhone: String?,
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    @SerialName("phone1")
    val phone: String?,
    val phone2: String?,
    val commune: String?,
    val street: String?,
    val number: String?,
    val activity: String?,
    val rccm: String?,
    val nif: String?,
    val ownerId: String?,
    val sector: String?,
    val created: String?
): Parcelable

@Keep
@Serializable
data class UpdateProfile(
    val customer: String,
    val tokenPhone: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone1: String,
    val phone2: String,
    val commune: String,
    val street: String,
    val number: String,
    val activity: String,
    val rccm: String,
    val nif: String,
    val ownerId: String,
    val sector: String
)

@Keep
@Serializable
data class DeletePaymentAccount(
    val code: String?,
    val customer: String?
)

@Keep
@Serializable
data class NotificationResponse(
    val message: String?,
    val status: String?,
    val notifications: List<Notification>?
)

@Keep
@Serializable
data class Notification(
    val reference: String?,
    val amount: String?,
    val description: String?,
    val date: String?,
    val customerName: String?,
    val merchantName: String?
)

@Keep
@Serializable
data class Commune(
    val code: String?,
    val name: String?
)

@Keep
@Serializable
data class HelpData(
    val helpPhoneNumber: String?,
    val helpEmail: String?,
    val helpText: String?
)

@Keep
@Serializable
data class Ticket(
    val client: String?,
    val ticket: String?,
    val date: String?,
    val paid: Boolean?
)

@Keep
@Serializable
data class TicketVerification(
    val code: String,
    val cleared: Boolean
)

@Keep
@Serializable
data class TicketVerificationResult(
    val status: String?,
    val message: String?,
    val purchaseAt: String?,
    val clearedAt: String?,
    val libelle: String?,
    val amount: String?
)

@Keep
@Serializable
data class Payment(
    val merchant: String,
    val phone: String,
    val currency: String,
    val reference: String,
    val amount: String,
    val fcm: String,
    val type: Int
)