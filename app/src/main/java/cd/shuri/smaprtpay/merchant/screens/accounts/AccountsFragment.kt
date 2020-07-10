package cd.shuri.smaprtpay.merchant.screens.accounts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import cd.shuri.smaprtpay.merchant.databinding.FragmentAccountsBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

/**
 * A simple [Fragment] subclass.
 */
class AccountsFragment : Fragment() {

    private lateinit var binding: FragmentAccountsBinding
    private val viewModel by viewModels<AccountsViewModel>()
    private  val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.paymentMethodRecyclerView.adapter = AccountsListAdapter()

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
    }

}
