package cd.shuri.smaprtpay.merchant.screens.ticket.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cd.shuri.smaprtpay.merchant.databinding.TicketItemBinding
import cd.shuri.smaprtpay.merchant.network.Ticket

class TicketAdapter(
    private val clickListener: TicketClickListener
):
    ListAdapter<Ticket, TicketAdapter.TicketViewHolder>(
        TicketDiffCallBack()
    )
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class TicketViewHolder private constructor(private val binding: TicketItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ticket,
                 clickListener: TicketClickListener
        ) {
            binding.ticket = item
            binding.clicklistener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TicketViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TicketItemBinding.inflate(layoutInflater, parent, false)
                return TicketViewHolder(binding)
            }
        }
    }
}