package cd.shuri.smaprtpay.merchant.screens.dotransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentDoTransferBinding
import cd.shuri.smaprtpay.merchant.network.TransferRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class DoTransferFragment : Fragment() {

    private lateinit var binding : FragmentDoTransferBinding

    private var selectedCurrency = ""
    private var selectedPaymentMethodMobile = ""
    private var selectedPaymentMethodCard = ""
    private val fcm = SmartPayApp.preferences.getString("fcm", "")

    val dialog = LoaderDialog()

    private val viewModel by viewModels<DoTransferViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoTransferBinding.inflate(layoutInflater)

        val items = listOf("USD", "CDF")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
        (binding.currencyTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currencyAuto.setOnItemClickListener { adapterView, _, i, _ ->
            Timber.d("user select ${adapterView.getItemAtPosition(i)}")
            selectedCurrency = if (adapterView.getItemAtPosition(i) == "USD") {
                "usd"
            } else {
                "cdf"
            }
        }

        binding.cardAuto.setOnItemClickListener { adapterView, _, i, _ ->
            val selection = adapterView.getItemAtPosition(i).toString()
            Timber.d("user select $selection")
            val split = selection.split(' ')
            val value = split.last()
            Timber.d("value: $value")
            for (e in viewModel.paymentMethods.value!!) {
                if (e.card == value) {
                    selectedPaymentMethodCard = e.code!!
                }
            }
        }

        binding.mobileAuto.setOnItemClickListener { adapterView, _, i, _ ->
            val selection = adapterView.getItemAtPosition(i).toString()
            Timber.d("user select $selection")
            val split = selection.split(' ')
            val value = split.last().replaceFirst("0", "243")
            Timber.d("value: $value")
            for (e in viewModel.paymentMethods.value!!) {
                if (e.phone == value) {
                    selectedPaymentMethodMobile = e.code!!
                }
            }
        }

        binding.validateButton.setOnClickListener {
            val valid = viewModel.validateForm(
                TransferRequest(
                    viewModel.userCode,
                    selectedPaymentMethodMobile,
                    selectedPaymentMethodCard,
                    selectedCurrency,
                    binding.cvvTet.text.toString(),
                    binding.amountTet.text.toString(),
                    fcm
                )
            )

            if (valid) {
//                viewModel.sendMoneyToEMoney(
//                    TransferRequest(
//                        viewModel.userCode,
//                        selectedPaymentMethodMobile,
//                        selectedPaymentMethodCard,
//                        selectedCurrency,
//                        binding.cvvTet.text.toString(),
//                        binding.amountTet.text.toString(),
//                        fcm
//                    )
//                )
            }
        }
    }


    private fun observers() {
        viewModel.paymentMethods.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val mobileAccountItems = mutableListOf<String>()
                val cardAccountItems = mutableListOf<String>()
                for (e in it) {
                    if (e.type == 1) {
                        mobileAccountItems.add("Mobile Money - ${e.phone!!.replaceFirst("243", "0")}")
                    } else {
                        cardAccountItems.add("Carte - ${e.card}")
                    }
                }
                var accountAdapter =
                    ArrayAdapter(requireContext(), R.layout.list_items, mobileAccountItems)
                (binding.mobileTil.editText as? AutoCompleteTextView)?.setAdapter(accountAdapter)
                accountAdapter =
                    ArrayAdapter(requireContext(), R.layout.list_items, cardAccountItems)
                (binding.cardTil.editText as? AutoCompleteTextView)?.setAdapter(accountAdapter)
            }
        }

        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }

        viewModel.isAmountValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.amountTil.error = null
            } else {
                binding.amountTil.error = "Montant invalide"
            }
        }

        viewModel.isCurrencySelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.currencyTil.error = null
            } else {
                binding.currencyTil.error = "Séléctionner une devise"
            }
        }

        viewModel.isCardSelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardTil.error = null
            } else {
                binding.cardTil.error = "Séléctionner votre compte carte"
            }
        }

        viewModel.isMobileSelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.mobileTil.error = null
            } else {
                binding.mobileTil.error = "Séléctionner votre compte mobile"
            }
        }

        viewModel.isCvvValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.cvvTil.error = null
            } else {
                binding.cvvTil.error = "CVV invalide"
            }
        }

        viewModel.transferCardToEMoneyStatus.observe(viewLifecycleOwner) {
            if (it != "0") {
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                    .setMessage("Votre approvisionnement a échoué")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        viewModel.navigateToTransfersFragment.observe(viewLifecycleOwner) {
            it.let {
                findNavController().navigate(
                    DoTransferFragmentDirections.actionDoTransferFragmentToTransfersFragment()
                )
                viewModel.navigateToTransfersFragmentDone()
            }
        }

        viewModel.showTToastForError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        }
    }
}