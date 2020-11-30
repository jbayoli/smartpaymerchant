package cd.shuri.smaprtpay.merchant.screens.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.FragmentAccountsBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class AccountsFragment : Fragment() {

    private lateinit var binding: FragmentAccountsBinding
    private val viewModel by viewModels<AccountsViewModel>()
    private  val dialog = LoaderDialog()
    private lateinit var accountsListAdapter: AccountsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        accountsListAdapter = AccountsListAdapter(
            EditAccountClickListener {
                val builder = MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_AppTheme_Dialog
                )
                    .setTitle("Validation")
                    .setMessage("Un code de validation vous sera envoyé pour valider la modification du compte de paiement")
                    .setPositiveButton("OK") { dialog, _ ->
                        findNavController().navigate(AccountsFragmentDirections.actionAccountsFragmentToEditPaymentAccountFragment(it))
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            },
            DeleteAccountClickListener {
                val builder =
                    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                        .setMessage("Vous êtes sur le point de supprimer votre compte de paiement")
                        .setPositiveButton("Supprrimer") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deletePaymentAccount(it)
                        }
                        .setNegativeButton("Annuler") { dialog, _ ->
                            dialog.dismiss()
                        }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            }
        )

        binding.paymentMethodRecyclerView.adapter = accountsListAdapter

        observers()
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

        viewModel.deleteResponse.observe(viewLifecycleOwner, {
            Timber.d("$it")
            if (it.status != "0") {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                accountsListAdapter.notifyItemRemoved(viewModel.indexOfRemovedAccount)
            }
        })
    }

}
