package cd.shuri.smaprtpay.merchant.screens.payment

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
import cd.shuri.smaprtpay.merchant.databinding.PaymentFragmentBinding
import cd.shuri.smaprtpay.merchant.network.Payment
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber
import java.util.*

class PaymentFragment : Fragment() {

    private val viewModel by viewModels<PaymentViewModel>()
    private lateinit var binding: PaymentFragmentBinding
    private val fcm = SmartPayApp.preferences.getString("fcm", "")
    val userCode = SmartPayApp.preferences.getString("name", "")
    private var selectedCurrency = ""
    private  val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentFragmentBinding.inflate(layoutInflater)
        val items = listOf("USD", "CDF")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
        (binding.currencyTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        observers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currencyAutoCompleteTv.setOnItemClickListener { adapterView, _, i, _ ->
            Timber.d("user select ${adapterView.getItemAtPosition(i)}")
            selectedCurrency = if (adapterView.getItemAtPosition(i) == "USD") {
                "usd"
            } else {
                "cdf"
            }
        }

        binding.validateButton.setOnClickListener {
            checkRequiredField()
        }
    }

    private fun checkRequiredField() {
        val phoneNumber = binding.phoneNumber.text.toString()
        val reference = binding.refTv.text.toString()
        val amount = binding.amountTet.text.toString()
        selectedCurrency

        val dataToCheck = mapOf(
            binding.amountTil to phoneNumber,
            binding.refTil to reference,
            binding.amountTil to amount,
            binding.currencyTil to selectedCurrency,
        )

        Timber.d("$dataToCheck")

        if (
            phoneNumber.isNotEmpty() &&
            reference.isNotEmpty() &&
            amount.isNotEmpty() &&
            selectedCurrency.isNotEmpty()
        ) {
            setErrorNullInAllFields()

            if(isPhoneNumberValid()) {
                payment()
            }
        } else {
            for ((k, v) in dataToCheck) {
                setErrorOnAField(v, k)
            }
        }
    }

    private fun setErrorNullInAllFields() {
        binding.accountTil.error = null
        binding.refTil.error = null
        binding.amountTil.error = null
        binding.currencyTil.error = null
    }


    private fun setErrorOnAField(
        data: String,
        textInputLayout: TextInputLayout
    ) {
        if (data.isEmpty()) {
            textInputLayout.error = "Ce champ est obligatoire"
        } else {
            textInputLayout.error = null
        }
    }

    private fun isPhoneNumberValid(): Boolean {
        val phoneNumber = binding.phoneNumber.text.toString()
        return if (phoneNumber.length == 10) {
            binding.accountTil.error = null
            true
        } else {
            binding.accountTil.error = "N° téléphone invalide"
            false
        }
    }

    private fun payment() {
        val request = Payment(
            userCode!!.uppercase(Locale.ROOT),
            binding.accountTil.editText!!.text.toString().replaceFirst("0", "243"),
            selectedCurrency,
            binding.refTv.text.toString(),
            binding.amountTet.text.toString(),
            fcm!!,
            1
        )

        viewModel.makePayment(request)
    }

    private fun observers() {
        observeShowDialog()
        observeShowToast()
        observeResponse()
    }

    private fun observeResponse() {
        viewModel.response.observe(viewLifecycleOwner) {
            it?.let {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Message")
                    .setMessage(it.message)
                    .setPositiveButton("Ok") { _, _ ->
                        findNavController().navigate(
                            PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                        )
                    }.show()
            }
        }
    }

    private fun observeShowToast() {
        viewModel.showTToastForError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone2()
            }
        }
    }

    private fun observeShowDialog() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }
    }
}