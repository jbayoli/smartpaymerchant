package cd.shuri.smaprtpay.merchant.screens.ticket.result

import cd.shuri.smaprtpay.merchant.network.Ticket

class TicketClickListener(val clickListener : (Ticket) -> Unit) {
    fun onclick(ticket: Ticket) = clickListener(ticket)
}
