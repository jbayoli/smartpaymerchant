package cd.shuri.smaprtpay.merchant.screens.addaccount

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
import cd.shuri.smaprtpay.merchant.NavGraphDirections
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp

import cd.shuri.smaprtpay.merchant.databinding.FragmentAddAccountBinding
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class AddAccountFragment : Fragment() {

    private lateinit var binding: FragmentAddAccountBinding
    private val viewModel by viewModels<AddAccountViewModel>()
    private val dialog = LoaderDialog()
    private val items = mutableListOf<String>()
    private var accountType = 0
    private var operatorCode = ""
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
        binding = FragmentAddAccountBinding.inflate(layoutInflater)

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.list_items, months)
        val adapterYear = ArrayAdapter(requireContext(), R.layout.list_items, years)

        (binding.monthTil.editText as? AutoCompleteTextView)?.setAdapter(adapterMonth)
        (binding.yearTil.editText as? AutoCompleteTextView)?.setAdapter(adapterYear)

        val dialog = AddAccountDialogFragment(viewModel)
        dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")

        Timber.d("use: $useCode")

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
            for (element in viewModel.providers.value!!) {
                if (element.name == adapterView.getItemAtPosition(i)) {
                    operatorCode = element.code!!
                }
            }
        }

        binding.cardOperatorAuto.setOnItemClickListener { adapterView, _, i, _ ->
            for (element in viewModel.providers.value!!) {
                if (element.name == adapterView.getItemAtPosition(i)) {
                    operatorCode = element.code!!
                }
            }
        }

        binding.validateButton.setOnClickListener {
            addPaymentMethod()
        }
    }

    private fun addPaymentMethod(){
        if (accountType == 1) {
            val valid = viewModel.validateFormMobile(AddPaymentMethodRequest(
                operator =  operatorCode,
                type = accountType,
                phone = binding.phoneNumberTet.text.toString(),
                customer = useCode!!,
                shortCode = binding.shortCodeTet.text.toString()
            ))
            if (valid) {
                viewModel.addPaymentMethod(
                    AddPaymentMethodRequest(
                        operator =  operatorCode,
                        type = accountType,
                        phone = "${binding.countryCodeTet.text.toString().removePrefix("+")}${binding.phoneNumberTet.text.toString()}",
                        customer = useCode,
                        shortCode = binding.shortCodeTet.text.toString()
                    )
                )
            } else {
                return
            }
        } else {
            val valid = viewModel.validateFormCard(AddPaymentMethodRequest(
                operator =  operatorCode,
                type = accountType,
                card = binding.cardNumberTet.text.toString(),
                expiration = "$selectedMonth/$selectedYear",
                customer = useCode!!,
                shortCode = binding.shortCodeTet.text.toString(),
                cardName = binding.cardNameTet.toString()
            ))

            if (valid) {
                if (binding.isMobileAccount.isChecked) {
                    viewModel.addPaymentMethod(AddPaymentMethodRequest(
                        operator =  operatorCode,
                        type = accountType,
                        card = binding.cardNumberTet.text.toString(),
                        expiration = "$selectedMonth/$selectedYear",
                        customer = useCode,
                        shortCode = binding.shortCodeTet.text.toString(),
                        cardName = binding.cardNameTet.toString()
                    ))
                } else {
                    viewModel.addPaymentMethod(AddPaymentMethodRequest(
                        operator =  operatorCode,
                        type = accountType,
                        card = binding.cardNumberTet.text.toString(),
                        expiration = "$selectedMonth/$selectedYear",
                        customer = useCode,
                        shortCode = binding.shortCodeTet.text.toString(),
                        cardName = binding.cardNameTet.toString()
                    ))
                }

            } else {
                return
            }
        }
    }

    private fun observers() {

        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
        viewModel.accountType.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                binding.mobileMoney.visibility = View.VISIBLE
                binding.validateButton.visibility = View.VISIBLE
                (binding.mobileProviderTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                accountType = it
            } else if (it == 2) {
                binding.card.visibility = View.VISIBLE
                binding.validateButton.visibility = View.VISIBLE
                (binding.cardOperatorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                accountType = it
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

        viewModel.isPhoneNumberEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.phoneNumberTil.error = null
            }
        })

        viewModel.isCardNameValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardNameTil.error = null
            } else {
                binding.cardNameTil.error = "Ce champ est obligatoir"
            }
        })

        viewModel.isCardNumberEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.cardNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNumberTil.error = null
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

        viewModel.showToastSuccess.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "${viewModel.messageAddAccount.value}", Toast.LENGTH_SHORT).show()
                viewModel.showToastSuccessDone()
            }
        })

        viewModel.showToastError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "${viewModel.messageAddAccount.value}", Toast.LENGTH_SHORT).show()
                viewModel.showToastErrorDone()
            }
        })

        viewModel.providers.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items.add(element.name!!)
                }
            }
        })

        viewModel.isPhoneNumberValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTil.error = null
            } else {
                binding.phoneNumberTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(AddAccountFragmentDirections.actionAddAccountFragmentToHomeFragment())
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
