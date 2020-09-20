package cd.shuri.smaprtpay.merchant.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.screens.accounts.AccountsListAdapter
import cd.shuri.smaprtpay.merchant.network.AccountsResponse
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import cd.shuri.smaprtpay.merchant.network.Transfers
import cd.shuri.smaprtpay.merchant.screens.transaction.TransactionListAdapter
import cd.shuri.smaprtpay.merchant.transactionvalidation.TransactionValidationListAdapter
import cd.shuri.smaprtpay.merchant.transfer.TransferListAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("setPhoneNumberError")
fun setPhoneNumberError(textLayout: TextInputLayout, phoneNumberCorrect: Boolean) {
    if (phoneNumberCorrect)
        textLayout.error = null
    else
        textLayout.error = "n° de téléphone invalide"
}

@BindingAdapter("setTimer")
fun setTimer(view: TextView, isTimerEnable: Boolean) {
    if (isTimerEnable) {
        view.visibility = View.VISIBLE
    }
}

@BindingAdapter("setCodeError")
fun setCodeError(textLayout: TextInputLayout, isCodeCorrect: Boolean) {
    if (isCodeCorrect) {
        textLayout.error = null
    } else {
        textLayout.error = "Code invalide"
    }
}

@BindingAdapter("setShortCodeOrCardExp")
fun setShortCodeOrCardExp(textView: TextView, account: AccountsResponse) {
    if (account.type == 1) {
        textView.text = "ShortCode :"
    } else {
        textView.text = "Date d'expiration :"
    }
}

@BindingAdapter("setPhoneOrCardNum")
fun setPhoneOrCardNum(textView: TextView, account: AccountsResponse) {
    if (account.type == 1) {
        textView.text = "Numéro :"
    } else {
        textView.text = "Carte :"
    }
}

@BindingAdapter("setShortCodeOrCardExpValue")
fun setShortCodeOrCardExpValue(textView: TextView, account: AccountsResponse) {
    if (account.type == 1) {
        textView.text = account.shortCode
    } else {
        textView.text = account.expiration
    }
}

@BindingAdapter("setAccountsType")
fun setAccountsType(textView: TextView, account: AccountsResponse) {
    if (account.type == 1) {
        textView.text = "Mobile Money"
    } else {
        textView.text = "Carte"
    }
}

@BindingAdapter("setPhoneOrCardNumValue")
fun setPhoneOrCardNumValue(textView: TextView, account: AccountsResponse) {
    if (account.type == 1) {
        account.phone?.let {
            textView.text = if (it.startsWith("243")) {
                it.replace("243", "0")
            } else {
                it
            }
        }
    } else {
        textView.text = account.card
    }
}

@BindingAdapter("enableResendButton")
fun enableResendButton(button: MaterialButton, isEnable: Boolean) {
    button.isEnabled = isEnable
}

@BindingAdapter("bindAccountsRecyclerView")
fun bindAccountsRecyclerView(recyclerView: RecyclerView, accounts: List<AccountsResponse>) {
    val adapter = recyclerView.adapter as AccountsListAdapter
    adapter.submitList(accounts)
}

@BindingAdapter("bindTransactionsRecyclerView")
fun bindTransactionsRecyclerView(recyclerView: RecyclerView, transactions: List<TransactionResponse>) {
    val adapter = recyclerView.adapter as TransactionListAdapter
    adapter.submitList(transactions)
}

@BindingAdapter("bindTransactionsToValidateRecyclerView")
fun bindTransactionsToValidateRecyclerView(recyclerView: RecyclerView, transactions: List<TransactionResponse>) {
    val adapter = recyclerView.adapter as TransactionValidationListAdapter
    adapter.submitList(transactions)
}

@BindingAdapter("bindNoAccount")
fun bindNoAccount(textView: TextView, accounts: List<AccountsResponse>) {
    if (accounts.isEmpty()) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.GONE
    }
}

@BindingAdapter("bindTransfersStatus")
fun bindTransfersStatus(img: ImageView, status: Int) {
    when (status) {
        2 -> {
            img.setImageResource(R.drawable.ic_round_check_circle_24)
        }
        0 -> {
            img.setImageResource(R.drawable.ic_round_remove_circle_24)
        }
        else -> {
            img.setImageResource(R.drawable.ic_round_pause_circle_filled_24)
        }
    }
}

@BindingAdapter("bindTransactionStatus")
fun bindTransactionStatus(img: ImageView, status: Boolean) {
    if (status) {
        img.setImageResource(R.drawable.ic_round_check_circle_24)
    } else {
        img.setImageResource(R.drawable.ic_round_remove_circle_24)
    }
}

@BindingAdapter("hideNoTransactionTextView")
fun hideNoTransactionTextView(textView: TextView, isTransactionDone: Boolean) {
    if (isTransactionDone) {
        textView.visibility = View.GONE
    } else {
        textView.visibility = View.VISIBLE
    }
}

@BindingAdapter("bindTransferRecyclerView")
fun bindTransferRecyclerView(recyclerView: RecyclerView, transfers: List<Transfers>) {
    val adapter = recyclerView.adapter as TransferListAdapter
    adapter.submitList(transfers)
}

@BindingAdapter("makeAccountEditable")
fun makeAccountEditable(imageView: ImageView, account: AccountsResponse) {
    if (account.type == 1) {
        imageView.visibility = View.GONE
    } else {
        imageView.visibility = View.VISIBLE
    }
}
