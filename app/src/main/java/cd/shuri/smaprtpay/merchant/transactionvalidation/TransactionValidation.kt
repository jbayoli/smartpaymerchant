package cd.shuri.smaprtpay.merchant.transactionvalidation

import android.app.NotificationManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentTransactionValidationBinding
import cd.shuri.smaprtpay.merchant.network.TransactionValidationRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import cd.shuri.smaprtpay.merchant.utilities.cancelNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var adapter: TransactionValidationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotification()

        binding = FragmentTransactionValidationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        Timber.d("fcm: $fcm")

        adapter =  TransactionValidationListAdapter(
            TransactionValidationListListenerV {
                viewModel.validateTransaction(TransactionValidationRequest(
                    it.code!!,
                    userCode!!,
                    true,
                    fcm!!
                ), it)
            }, TransactionValidationListListenerC {
                viewModel.validateTransaction(TransactionValidationRequest(
                    it.code!!,
                    userCode!!,
                    false,
                    fcm!!
                ), it)
            })

        binding.transactionsToValidateRecyclerView.adapter = adapter

        observers()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun observers() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                }  else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        })

        viewModel.validation.observe(viewLifecycleOwner, {
            it?.let {
                val builder = if (it.status != "0") {
                    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                        .setMessage(it.message)
                        .setPositiveButton("OK") {dialog, _ ->
                            dialog.dismiss()
                        }
                } else {
                    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                        .setMessage(it.message)
                        .setPositiveButton("OK") {dialog, _ ->
                            dialog.dismiss()
                            viewModel.navigateToHome()
                        }
                }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, {
            if (it != null) {
                findNavController().navigate(TransactionValidationDirections.actionTransactionValidationToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        })

        viewModel.indexRemoved.observe( viewLifecycleOwner, {
            adapter.notifyItemRemoved(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transaction_validation_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh -> {
                viewModel.getTransactions()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

}
