package cd.shuri.smaprtpay.merchant.screens.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import cd.shuri.smaprtpay.merchant.databinding.TransactionsErrorFragmentBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

class TransactionsErrorFragment : Fragment() {

    private val viewModel by viewModels<TransactionsErrorViewModel>()

    private lateinit var binding : TransactionsErrorFragmentBinding

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TransactionsErrorFragmentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.transactionsValidatedRecyclerView.adapter = TransactionListAdapter()

        observers()
        return binding.root
    }

    private fun observers() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                }  else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        })
    }
}