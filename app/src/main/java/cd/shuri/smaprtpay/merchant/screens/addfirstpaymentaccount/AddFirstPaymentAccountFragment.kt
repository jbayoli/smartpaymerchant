package cd.shuri.smaprtpay.merchant.screens.addfirstpaymentaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentAddFirstPaymentAccountBinding
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodFirstTimeRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber
import java.util.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddFirstPaymentAccountFragment : Fragment() {

    private lateinit var binding: FragmentAddFirstPaymentAccountBinding

    private val viewModel by viewModels<AddPaymentAccountViewModel>()

    private val dialog = LoaderDialog()
    private val items1 = mutableListOf<String>()
    private val items2 = mutableListOf<String>()

    private var operatorCode1 = ""
    private var operatorCode2 = ""
    private val useCode = SmartPayApp.preferences.getString("user_code", "")
    private val defaultPhone = SmartPayApp.preferences.getString("default_phone", null)
    private val months = mutableListOf<String>()
    private val years = mutableListOf<Int>()
    private var selectedMonth = ""
    private var selectedYear = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (e in 1..12) {
            if (e in 1..9) {
                months.add(e - 1, "0$e")
            } else {
                months.add(e - 1, "$e")
            }
        }

        var yearIndex = 0
        for (y in 20..50) {
            years.add(yearIndex, y)
            yearIndex += 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddFirstPaymentAccountBinding.inflate(layoutInflater)
        binding.phoneNumberTet.setText(defaultPhone)
        binding.phoneNumberTet.isEnabled = false

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("isAccountDone", "false")
        preferencesEditor.apply()

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.list_items, months)
        val adapterYear = ArrayAdapter(requireContext(), R.layout.list_items, years)

        (binding.monthTil.editText as? AutoCompleteTextView)?.setAdapter(adapterMonth)
        (binding.yearTil.editText as? AutoCompleteTextView)?.setAdapter(adapterYear)

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthAuto.setOnItemClickListener { adapterView, _, i, _ ->
            selectedMonth = adapterView.getItemAtPosition(i).toString()
        }

        binding.yearAuto.setOnItemClickListener { adapterView, _, i, _ ->
            selectedYear = "${adapterView.getItemAtPosition(i)}"
        }

        binding.mobileMoneyAuto.setOnItemClickListener { adapterView, _, i, _ ->
            Timber.d("user select ${adapterView.getItemAtPosition(i)}")
            for (element in viewModel.providersPhone.value!!) {
                if (element.name == adapterView.getItemAtPosition(i)) {
                    operatorCode1 = element.code!!
                }
            }
        }

        binding.cardOperatorAuto.setOnItemClickListener { adapterView, _, i, _ ->
            for (element in viewModel.providersCard.value!!) {
                if (element.name == adapterView.getItemAtPosition(i)) {
                    operatorCode2 = element.code!!
                    if (element.name?.toLowerCase(Locale.ROOT) != "visa" &&
                        element.name?.toLowerCase(Locale.ROOT) != "mastercard") {
                        binding.expirationDateTv.visibility = View.GONE
                        binding.expirationDateContent.visibility = View.GONE
                        binding.cardNameTil.visibility = View.GONE
                    } else {
                        binding.cardNameTil.visibility = View.VISIBLE
                        binding.expirationDateTv.visibility = View.VISIBLE
                        binding.expirationDateContent.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.validateButton.setOnClickListener {
            addPaymentMethod()
        }

        binding.mobileMoneyAuto.addTextChangedListener {
            binding.phoneNumberTil.error = null
        }

        binding.cardOperatorAuto.addTextChangedListener {
            binding.cardNameTil.error = null
            binding.cardNumberTil.error = null
            binding.monthTil.error = null
            binding.yearTil.error = null
        }

        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            if (i == R.id.radio_yes) {
                Timber.d("Yes")
                binding.radioYes.isChecked = true
            } else {
                Timber.d("No")
                binding.radioNo.isChecked = true
            }
        }
    }

    private fun addPaymentMethod() {
        val phoneNumber = "${
            binding.countryCodeTet.text.toString().removePrefix("+")
        }${binding.phoneNumberTet.text.toString()}"

        if (binding.mobileMoneyAuto.text.isEmpty()) {
            operatorCode1 = ""
        }

        if (binding.cardOperatorAuto.text.isEmpty()) {
            operatorCode2 = ""
        }

        val shortCode = if (binding.shortCodeTet.text.toString().isNotEmpty()) {
            binding.shortCodeTet.text.toString()
        } else {
            ""
        }


        when {
            operatorCode1.isEmpty() -> {
                val request = AddPaymentMethodFirstTimeRequest(
                    null,
                    operatorCode2,
                    0,
                    2,
                    null,
                    binding.cardNumberTet.text.toString(),
                    "$selectedMonth/$selectedYear",
                    useCode!!,
                    "",
                    binding.cardNameTet.text.toString()
                )
                val valid = viewModel.validateForm(request)
                if (valid) {
                    viewModel.addPaymentMethod(request)
                } else {
                    return
                }
            }
            operatorCode2.isEmpty() -> {
                val request = AddPaymentMethodFirstTimeRequest(
                    operatorCode1,
                    null,
                    1,
                    0,
                    phoneNumber,
                    null,
                    null,
                    useCode!!,
                    shortCode,
                    null
                )
                val valid = viewModel.validateForm(request)
                if (valid) {
                    viewModel.addPaymentMethod(request)
                } else {
                    return
                }
            }
            else -> {
                val request = AddPaymentMethodFirstTimeRequest(
                    operatorCode1,
                    operatorCode2,
                    1,
                    2,
                    phoneNumber,
                    binding.cardNumberTet.text.toString(),
                    "$selectedMonth/$selectedYear",
                    useCode!!,
                    shortCode,
                    binding.cardNameTet.text.toString()
                )
                val valid = viewModel.validateForm(request)
                if (valid) {
                    viewModel.addPaymentMethod(request)
                } else {
                    return
                }
            }
        }
    }

    private fun observers() {
        viewModel.showToastError.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    "${viewModel.messageAddAccount.value}",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        }

        viewModel.providersCard.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items2.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items2)
                (binding.cardOperatorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        viewModel.providersPhone.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items1.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items1)
                (binding.mobileProviderTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }

        viewModel.isPhoneNumberEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.phoneNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.phoneNumberTil.error = null
            }
        }

        viewModel.isCardNumberEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNumberTil.error = null
            }
        }

        viewModel.isCardNameEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardNameTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNameTil.error = null
            }
        }

        viewModel.isExpirationValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.monthTil.error = null
                binding.yearTil.error = null
            } else {
                binding.monthTil.error = "Invalide"
                binding.yearTil.error = "Invalide"
            }
        }

        viewModel.isPhoneNumberValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.phoneNumberTil.error = null
            } else {
                binding.phoneNumberTil.error = "n° de téléphone invalide"
            }
        }

        viewModel.isCardProviderSelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardOperatorTil.error = null
            } else {
                binding.cardOperatorTil.error = "Séléctionner d'abord un opérateur"
            }
        }

        viewModel.isMobileProviderSelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.mobileProviderTil.error = null
            } else {
                binding.mobileProviderTil.error = "Séléctionner d'abord un opérateur"
            }
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(AddFirstPaymentAccountFragmentDirections.actionAddFirstPaymentAccountFragmentToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        }

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
}