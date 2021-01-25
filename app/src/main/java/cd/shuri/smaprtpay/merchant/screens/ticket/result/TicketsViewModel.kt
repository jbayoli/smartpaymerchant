package cd.shuri.smaprtpay.merchant.screens.ticket.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cd.shuri.smaprtpay.merchant.network.Ticket

class TicketsViewModel(tickets : List<Ticket>) : ViewModel() {
    private val _ticketsLiveData = MutableLiveData(tickets)
    val ticketsLiveData: LiveData<List<Ticket>> get() = _ticketsLiveData
}