package cd.shuri.smaprtpay.merchant.transactionvalidation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentTransactionValidationBinding
import cd.shuri.smaprtpay.merchant.network.TransactionValidationRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class TransactionValidation : Fragment() {

    private lateinit var binding: FragmentTransactionValidationBinding

    private val viewModel by viewModels<TransactionValidationViewModel>()

    private val dialog = LoaderDialog()

    private val userCode = SmartPayApp.preferences.getString("user_code", "")
    private val fcm = SmartPayApp.preferences.getString("fcm", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionValidationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        Timber.d("fcm: $fcm")

        binding.transactionsToValidateRecyclerView.adapter = TransactionValidationListAdapter(
            TransactionValidationListListenerV {
                viewModel.validateTransaction(TransactionValidationRequest(
                    it.code!!,
                    userCode!!,
                    true,
                    fcm!!
                ))
            }, TransactionValidationListListenerC {
                viewModel.validateTransaction(TransactionValidationRequest(
                    it.code!!,
                    userCode!!,
                    false,
                    fcm!!
                ))
            })

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

        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), viewModel.messageValidation.value, Toast.LENGTH_SHORT).show()
                viewModel.showToastDone()
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(TransactionValidationDirections.actionTransactionValidationToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        })
    }

}
