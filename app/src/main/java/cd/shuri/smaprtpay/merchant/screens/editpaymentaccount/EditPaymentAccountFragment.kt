package cd.shuri.smaprtpay.merchant.screens.editpaymentaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.EditPaymentAccountFragmentBinding
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

class EditPaymentAccountFragment : Fragment() {

    private lateinit var binding: EditPaymentAccountFragmentBinding
    private lateinit var args: EditPaymentAccountFragmentArgs
    private lateinit var viewModel: EditPaymentAccountViewModel
    private var operatorCode = ""
    private val useCode = SmartPayApp.preferences.getString("user_code", "")
    private val items = mutableListOf<String>()
    private val months = mutableListOf<String>()
    private val years = mutableListOf<Int>()
    private var selectedMonth = ""
    private var selectedYear = ""
    private var accountType = 0
    private var accountCode = ""
    private val dialog = LoaderDialog()

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
        args = EditPaymentAccountFragmentArgs.fromBundle(requireArguments())
        binding = EditPaymentAccountFragmentBinding.inflate(layoutInflater)

        operatorCode = args.account.operator!!
        accountCode = args.account.code!!

        val viewModelFactory = EditPaymentAccountViewModelFactory(args.account.type!!)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(EditPaymentAccountViewModel::class.java)

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.list_items, months)
        val adapterYear = ArrayAdapter(requireContext(), R.layout.list_items, years)

        (binding.monthTil.editText as? AutoCompleteTextView)?.setAdapter(adapterMonth)
        (binding.yearTil.editText as? AutoCompleteTextView)?.setAdapter(adapterYear)

        showProperForm()

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

    private fun addPaymentMethod() {
        if (accountType == 1) {
            val valid = viewModel.validateFormMobile(
                AddPaymentMethodRequest(
                    operator = operatorCode,
                    type = accountType,
                    phone = binding.phoneNumberTet.text.toString(),
                    customer = useCode!!,
                    shortCode = binding.shortCodeTet.text.toString(),
                    code = accountCode
                )
            )
            if (valid) {
                viewModel.editPaymentMethod(
                    AddPaymentMethodRequest(
                        operator = operatorCode,
                        type = accountType,
                        phone = "${
                            binding.countryCodeTet.text.toString().removePrefix("+")
                        }${binding.phoneNumberTet.text.toString()}",
                        customer = useCode,
                        shortCode = binding.shortCodeTet.text.toString(),
                        code = accountCode
                    )
                )
            } else {
                return
            }
        } else {
            val valid = viewModel.validateFormCard(
                AddPaymentMethodRequest(
                    operator = operatorCode,
                    type = accountType,
                    card = binding.cardNumberTet.text.toString(),
                    expiration = "$selectedMonth/$selectedYear",
                    customer = useCode!!,
                    shortCode = binding.shortCodeTet.text.toString(),
                    code = accountCode
                )
            )

            if (valid) {
                if (binding.isMobileAccount.isChecked) {
                    viewModel.editPaymentMethod(
                        AddPaymentMethodRequest(
                            operator = operatorCode,
                            type = accountType,
                            card = binding.cardNumberTet.text.toString(),
                            expiration = "$selectedMonth/$selectedYear",
                            customer = useCode,
                            shortCode = binding.shortCodeTet.text.toString(),
                            code = accountCode
                        )
                    )
                } else {
                    viewModel.editPaymentMethod(
                        AddPaymentMethodRequest(
                            operator = operatorCode,
                            type = accountType,
                            card = binding.cardNumberTet.text.toString(),
                            expiration = "$selectedMonth/$selectedYear",
                            customer = useCode,
                            shortCode = binding.shortCodeTet.text.toString(),
                            code = accountCode
                        )
                    )
                }

            } else {
                return
            }
        }
    }

    private fun showProperForm() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
        if (args.account.type == 1) {
            binding.phoneNumberTet.setText(args.account.phone?.replaceFirst("243", ""))
            binding.shortCodeTet.setText(args.account.shortCode)
            binding.mobileMoneyAuto.setText(args.account.operatorName)
            binding.mobileMoney.visibility = View.VISIBLE
            binding.validateButton.visibility = View.VISIBLE
            (binding.mobileProviderTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            accountType = args.account.type!!
        } else {
            binding.cardNameTet.setText(args.account.cardName)
            binding.cardNumberTet.setText(args.account.card)
            binding.monthAuto.setText(args.account.expiration?.split('/')?.get(0))
            binding.yearAuto.setText(args.account.expiration?.split('/')?.get(1))
            binding.cardOperatorAuto.setText(args.account.operatorName)
            binding.card.visibility = View.VISIBLE
            binding.validateButton.visibility = View.VISIBLE
            (binding.cardOperatorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            accountType = args.account.type!!
        }
    }

    private fun observers() {
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

        viewModel.showDialogLoader.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        })

        viewModel.providers.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (element in it) {
                    items.add(element.name!!)
                }
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

        viewModel.isPhoneNumberValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTil.error = null
            } else {
                binding.phoneNumberTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(EditPaymentAccountFragmentDirections.actionEditPaymentAccountFragmentToAccountsFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (it.status != "0") {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}