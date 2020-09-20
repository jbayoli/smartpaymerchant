package cd.shuri.smaprtpay.merchant.screens.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.AccountItemsBinding
import cd.shuri.smaprtpay.merchant.network.AccountsResponse

class AccountsListAdapter(
    private val editAccountClickListener: EditAccountClickListener,
    private val deleteAccountClickListener: DeleteAccountClickListener
) :
    ListAdapter<AccountsResponse, AccountsListAdapter.AccountsViewHolder>(AccountsListDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        return AccountsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        holder.bind(getItem(position),editAccountClickListener, deleteAccountClickListener)
    }

    class AccountsViewHolder private constructor(private val binding: AccountItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: AccountsResponse,
            editAccountClickListener: EditAccountClickListener,
            deleteAccountClickListener: DeleteAccountClickListener
        ) {
            binding.account = item
            binding.editClickListener = editAccountClickListener
            binding.deleteClickListener = deleteAccountClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AccountsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AccountItemsBinding.inflate(layoutInflater, parent, false)
                return AccountsViewHolder(binding)
            }
        }
    }
}

class AccountsListDiffCallBack : DiffUtil.ItemCallback<AccountsResponse>() {

    override fun areItemsTheSame(oldItem: AccountsResponse, newItem: AccountsResponse): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: AccountsResponse, newItem: AccountsResponse): Boolean {
        return oldItem == newItem
    }
}

class EditAccountClickListener(val clickListener: (AccountsResponse) -> Unit) {
    fun onClick(account: AccountsResponse) = clickListener(
        account
    )
}

class DeleteAccountClickListener(val clickListener: (AccountsResponse) -> Unit) {
    fun onClick(account: AccountsResponse) = clickListener(
        account
    )
}