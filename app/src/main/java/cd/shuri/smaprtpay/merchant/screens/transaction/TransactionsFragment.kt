package cd.shuri.smaprtpay.merchant.screens.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import cd.shuri.smaprtpay.merchant.databinding.FragmentTransactionsBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding

    private val viewModel by viewModels<TransactionsViewModel>()

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTransactionsBinding.inflate(layoutInflater)
        val adapter = TransactionListAdapter()
        binding.allTransactionsRecyclerView.adapter = adapter

        observers(adapter)
        return binding.root
    }

    private fun observers(adapter: TransactionListAdapter) {

        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                }  else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { state ->
                when(state.refresh) {
                    is LoadState.Loading -> viewModel.showDialogLoaderDone(true)
                    is LoadState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> { viewModel.showDialogLoaderDone(false)}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { state ->
                when(state.append) {
                    is LoadState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is LoadState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

}
