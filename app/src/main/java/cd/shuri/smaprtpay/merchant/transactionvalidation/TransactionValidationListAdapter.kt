package cd.shuri.smaprtpay.merchant.transactionvalidation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.TransactionsToValidateItemsBinding
import cd.shuri.smaprtpay.merchant.network.TransactionResponse

class TransactionValidationListAdapter(
    private val clickListenerV: TransactionValidationListListenerV,
    private val clickListenerC: TransactionValidationListListenerC
):
    ListAdapter<TransactionResponse, TransactionValidationListAdapter.TransactionValidationViewHolder>(
    TransactionValidationListDiffCallBack())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionValidationViewHolder {
        return TransactionValidationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TransactionValidationViewHolder, position: Int) {
        holder.bind(getItem(position), clickListenerV, clickListenerC)
    }

    class TransactionValidationViewHolder private constructor(private val binding: TransactionsToValidateItemsBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionResponse,
                 clickListenerV: TransactionValidationListListenerV,
                 clickListenerC: TransactionValidationListListenerC) {
            binding.clickListenerV = clickListenerV
            binding.clickListenerC = clickListenerC
            binding.transaction = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TransactionValidationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionsToValidateItemsBinding.inflate(layoutInflater, parent, false)
                return TransactionValidationViewHolder(binding)
            }
        }
    }
}

class TransactionValidationListDiffCallBack :  DiffUtil.ItemCallback<TransactionResponse>() {

    override fun areItemsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem == newItem
    }
}

class TransactionValidationListListenerC(val clickListener : (TransactionResponse) -> Unit) {
    fun onclick(transaction: TransactionResponse) = clickListener(transaction)
}

class TransactionValidationListListenerV(val clickListener : (TransactionResponse) -> Unit) {
    fun onclick(transaction: TransactionResponse) = clickListener(transaction)
}