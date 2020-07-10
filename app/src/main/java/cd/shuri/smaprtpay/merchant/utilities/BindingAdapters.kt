package cd.shuri.smaprtpay.merchant.utilities

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.screens.accounts.AccountsListAdapter
import cd.shuri.smaprtpay.merchant.network.AccountsResponse
import cd.shuri.smaprtpay.merchant.network.TransactionResponse
import cd.shuri.smaprtpay.merchant.screens.transaction.TransactionListAdapter
import cd.shuri.smaprtpay.merchant.transactionvalidation.TransactionValidationListAdapter
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
    if (account.type == "Mobile Money") {
        textView.text = "ShortCode :"
    } else {
        textView.text = "Date d'expiration :"
    }
}

@BindingAdapter("setPhoneOrCardNum")
fun setPhoneOrCardNum(textView: TextView, account: AccountsResponse) {
    if (account.type == "Mobile Money") {
        textView.text = "Numéro :"
    } else {
        textView.text = "Carte :"
    }
}

@BindingAdapter("enableResendButton")
fun enableResendButton(button: MaterialButton, isEnable: Boolean) {
    if (isEnable) {
        button.isEnabled = isEnable
    } else {
        button.isEnabled = isEnable
    }
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
