package cd.shuri.smaprtpay.merchant.screens.dotransfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
 * Use the [DoTransferFragment.newInstance] factory method to
 * create an instance of this fragment.
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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoTransferBinding.inflate(layoutInflater)

        val items = listOf("USD", "CDF")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
        (binding.currencyTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.amountTet.setText("0.00")

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
                if (e.value!! == value) {
                    selectedPaymentMethodCard = e.code!!
                }
            }
        }

        binding.mobileAuto.setOnItemClickListener { adapterView, _, i, _ ->
            val selection = adapterView.getItemAtPosition(i).toString()
            Timber.d("user select $selection")
            val split = selection.split(' ')
            val value = split.last().replaceRange(0, 1, "243")
            Timber.d("value: $value")
            for (e in viewModel.paymentMethods.value!!) {
                if (e.value!! == value) {
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
                viewModel.sendMoneyToEMoney(
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
            }
        }
    }


    private fun observers() {
        viewModel.paymentMethods.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val mobileAccountItems = mutableListOf<String>()
                val cardAccountItems = mutableListOf<String>()
                for (e in it) {
                    if (e.type == "Mobile Money") {
                        mobileAccountItems.add("${e.type} - ${e.value!!.replaceRange(0, 3, "0")}")
                    } else {
                        cardAccountItems.add("${e.type} - ${e.value}")
                    }
                }
                var accountAdapter =
                    ArrayAdapter(requireContext(), R.layout.list_items, mobileAccountItems)
                (binding.mobileTil.editText as? AutoCompleteTextView)?.setAdapter(accountAdapter)
                accountAdapter =
                    ArrayAdapter(requireContext(), R.layout.list_items, cardAccountItems)
                (binding.cardTil.editText as? AutoCompleteTextView)?.setAdapter(accountAdapter)
            }
        })

        viewModel.showDialogLoader.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        })

        viewModel.isAmountValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.amountTil.error = null
            } else {
                binding.amountTil.error = "Montant invalide"
            }
        })

        viewModel.isCurrencySelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.currencyTil.error = null
            } else {
                binding.currencyTil.error = "Séléctionner une devise"
            }
        })

        viewModel.isCardSelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardTil.error = null
            } else {
                binding.cardTil.error = "Séléctionner votre compte carte"
            }
        })

        viewModel.isMobileSelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mobileTil.error = null
            } else {
                binding.mobileTil.error = "Séléctionner votre compte mobile"
            }
        })

        viewModel.isCvvValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cvvTil.error = null
            } else {
                binding.cvvTil.error = "CVV invalide"
            }
        })

        viewModel.transferCardToEMoneyStatus.observe(viewLifecycleOwner, Observer {
            if (it != "0") {
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                    .setMessage("Votre transfert a échoué")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })

        viewModel.navigateToTransfersFragment.observe(viewLifecycleOwner, Observer {
            it.let {
                findNavController().navigate(
                    DoTransferFragmentDirections.actionDoTransferFragmentToTransfersFragment()
                )
                viewModel.navigateToTransfersFragmentDone()
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