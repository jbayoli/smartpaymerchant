package cd.shuri.smaprtpay.merchant.screens.ticket.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cd.shuri.smaprtpay.merchant.network.Ticket

class TicketViewModelFactory (private val tickets: List<Ticket>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketsViewModel::class.java)) {
            return TicketsViewModel(tickets) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}