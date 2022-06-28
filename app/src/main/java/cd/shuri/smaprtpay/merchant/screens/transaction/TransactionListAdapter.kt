package cd.shuri.smaprtpay.merchant.screens.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.TransactionItemsBinding
import cd.shuri.smaprtpay.merchant.network.TransactionResponse

class TransactionListAdapter :
    PagingDataAdapter<TransactionResponse, TransactionListAdapter.TransactionsViewHolder>(TransactionListDiffCallBack())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        return TransactionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class TransactionsViewHolder private constructor(private val binding: TransactionItemsBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionResponse) {
            binding.transaction = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TransactionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionItemsBinding.inflate(layoutInflater, parent, false)
                return TransactionsViewHolder(binding)
            }
        }
    }
}

class TransactionListDiffCallBack :  DiffUtil.ItemCallback<TransactionResponse>() {

    override fun areItemsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: TransactionResponse, newItem: TransactionResponse): Boolean {
        return oldItem == newItem
    }
}