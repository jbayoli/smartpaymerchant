package cd.shuri.smaprtpay.merchant.screens.addfirstpaymentaccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentAddFirstPaymentAccountBinding
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodFirstTimeRequest
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodRequest
import cd.shuri.smaprtpay.merchant.screens.addaccount.AddAccountDialogFragment
import cd.shuri.smaprtpay.merchant.screens.addaccount.AddAccountFragmentDirections
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddFirstPaymentAccountFragment : Fragment() {

    private lateinit var binding : FragmentAddFirstPaymentAccountBinding

    private val viewModel by viewModels<AddPaymentAccountViewModel>()

    private val dialog = LoaderDialog()
    private val items1 = mutableListOf<String>()
    private val items2 = mutableListOf<String>()

    private var operatorCode1 = ""
    private var operatorCode2 = ""
    private val useCode = SmartPayApp.preferences.getString("user_code", "")
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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddFirstPaymentAccountBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("isAccountDone", "false")
        preferencesEditor.apply()

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.list_items, months)
        val adapterYear = ArrayAdapter(requireContext(), R.layout.list_items, years)

        (binding.monthTil.editText as? AutoCompleteTextView)?.setAdapter(adapterMonth)
        (binding.yearTil.editText as? AutoCompleteTextView)?.setAdapter(adapterYear)

        observers()


        return  binding.root
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
                }
            }
        }

        binding.validateButton.setOnClickListener {
            addPaymentMethod()
        }
    }

    private fun addPaymentMethod(){
        val phoneNumber = "${binding.countryCodeTet.text.toString().removePrefix("+")}${binding.phoneNumberTet.text.toString()}"

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
                    null,
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
                    binding.shortCodeTet.text.toString(),
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
                    binding.phoneNumberTet.text.toString(),
                    binding.cardNumberTet.text.toString(),
                    "$selectedMonth/$selectedYear",
                    useCode!!,
                    binding.shortCodeTet.text.toString(),
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
        viewModel.showToastError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "${viewModel.messageAddAccount.value}", Toast.LENGTH_SHORT).show()
                viewModel.showToastErrorDone()
            }
        })

        viewModel.providersCard.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items2.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items2)
                (binding.cardOperatorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

        viewModel.providersPhone.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items1.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items1)
                (binding.mobileProviderTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

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

        viewModel.isPhoneNumberEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.phoneNumberTil.error = null
            }
        })

        viewModel.isShortCodeEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shortCodeTil.error = "Ce champ est obligatoir"
            } else {
                binding.shortCodeTil.error = null
            }
        })

        viewModel.isCardNumberEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNumberTil.error = null
            }
        })

        viewModel.isCardNameEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardNameTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNameTil.error = null
            }
        })

        viewModel.isExpirationValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.monthTil.error = null
                binding.yearTil.error = null
            } else {
                binding.monthTil.error = "Invalide"
                binding.yearTil.error = "Invalide"
            }
        })

        viewModel.isPhoneNumberValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTil.error = null
            } else {
                binding.phoneNumberTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.isCardProviderSelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardOperatorTil.error = null
            } else {
                binding.cardOperatorTil.error = "Séléctionner d'abord un opérateur"
            }
        })

        viewModel.isMobileProviderSelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mobileProviderTil.error = null
            } else {
                binding.mobileProviderTil.error = "Séléctionner d'abord un opérateur"
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(AddFirstPaymentAccountFragmentDirections.actionAddFirstPaymentAccountFragmentToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone2()
            }
        })

    }
}