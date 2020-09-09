package cd.shuri.smaprtpay.merchant.transfer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.TransferItemsBinding
import cd.shuri.smaprtpay.merchant.network.Transfers

class TransferListAdapter : ListAdapter<Transfers, TransferListAdapter.TransferViewHolder>(TransferListDiffCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        return TransferViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransferViewHolder private constructor(private val binding: TransferItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transfers) {
            binding.transfer = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup):  TransferViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransferItemsBinding.inflate(layoutInflater, parent, false)
                return TransferViewHolder(binding)
            }
        }
    }
}

class TransferListDiffCallBack: DiffUtil.ItemCallback<Transfers>() {

    override fun areItemsTheSame(oldItem: Transfers, newItem: Transfers): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Transfers, newItem: Transfers): Boolean {
        return oldItem == newItem
    }
}