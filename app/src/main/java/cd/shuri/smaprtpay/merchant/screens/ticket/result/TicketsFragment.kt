package cd.shuri.smaprtpay.merchant.screens.ticket.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cd.shuri.smaprtpay.merchant.databinding.TicketsFragmentBinding
import timber.log.Timber

class TicketsFragment : Fragment() {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsFragmentBinding
    private lateinit var adapter: TicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = TicketViewModelFactory(listOf())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(TicketsViewModel::class.java)
        binding = TicketsFragmentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = TicketAdapter(TicketClickListener {
            Timber.i("Print ${it.ticket}")
        })

        binding.ticketRecyclerView.adapter = adapter

        return  binding.root
    }


}