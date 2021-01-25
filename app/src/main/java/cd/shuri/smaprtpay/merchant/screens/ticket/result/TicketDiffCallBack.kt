package cd.shuri.smaprtpay.merchant.screens.ticket.result

import androidx.recyclerview.widget.DiffUtil
import cd.shuri.smaprtpay.merchant.network.Ticket

class TicketDiffCallBack: DiffUtil.ItemCallback<Ticket>() {

    override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem.ticket == newItem.ticket
    }

    override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem == newItem
    }
}