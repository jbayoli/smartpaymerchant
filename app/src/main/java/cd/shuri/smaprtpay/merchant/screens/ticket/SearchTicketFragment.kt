package cd.shuri.smaprtpay.merchant.screens.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cd.shuri.smaprtpay.merchant.databinding.SearchTicketFragmentBinding

class SearchTicketFragment : Fragment() {

    private val viewModel: SearchTicketViewModel by viewModels()
    private lateinit var binding: SearchTicketFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchTicketFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}