package cd.shuri.smaprtpay.merchant.screens.editpaymentaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.EditPaymentAccountFragmentBinding
import cd.shuri.smaprtpay.merchant.network.AddPaymentMethodRequest
import cd.shuri.smaprtpay.merchant.network.ProviderType
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

class EditPaymentAccountFragment : Fragment() {

    private lateinit var binding: EditPaymentAccountFragmentBinding
    private val args: EditPaymentAccountFragmentArgs by navArgs()
    private lateinit var viewModel: EditPaymentAccountViewModel
    private var operatorCode = ""
    private val useCode = SmartPayApp.preferences.getString("user_code", "")
    private val items = mutableListOf<String>()
    private val cardItems = mutableListOf<String>()
    private val months = mutableListOf<String>()
    private val years = mutableListOf<Int>()
    private var selectedMonth = ""
    private var selectedYear = ""
    private var accountType = 0
    private var accountCode = ""
    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditPaymentAccountFragmentBinding.inflate(layoutInflater)

        operatorCode = args.account.operator!!
        accountCode = args.account.code!!

        val viewModelFactory = EditPaymentAccountViewModelFactory(args.account.type!!)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(EditPaymentAccountViewModel::class.java)

        showProperForm()

        observers()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        for (e in 1..12) {
            if (e in 1..9) {
                months.add("0$e")
            } else {
                months.add("$e")
            }
        }


        for (y in 20..50) {
            years.add( y)
        }

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.list_items, months)
        val adapterYear = ArrayAdapter(requireContext(), R.layout.list_items, years)

        (binding.monthTil.editText as? AutoCompleteTextView)?.setAdapter(adapterMonth)
        (binding.yearTil.editText as? AutoCompleteTextView)?.setAdapter(adapterYear)
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
                    if (element.code != "visa" && element.code != "mastercard") {
                        binding.cardNameTil.visibility = View.GONE
                        binding.expirationDateTv.visibility = View.GONE
                        binding.expirationDateContent.visibility = View.GONE
                    } else {
                        binding.cardNameTil.visibility = View.VISIBLE
                        binding.expirationDateTv.visibility = View.VISIBLE
                        binding.expirationDateContent.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.validateButton.setOnClickListener {
            editPaymentMethod()
        }
    }

    private fun editPaymentMethod() {
        if (accountType == ProviderType.Mobile) {
            val valid = viewModel.validateFormMobile(
                AddPaymentMethodRequest(
                    operator = operatorCode,
                    type = accountType,
                    phone = binding.phoneNumberTet.text.toString(),
                    customer = useCode!!,
                    shortCode = binding.shortCodeTet.text.toString(),
                    code = accountCode,
                    isMerchant = "0"
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
                        code = accountCode,
                        isMerchant = "0"
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
                    cardName = binding.cardNameTet.text.toString(),
                    expiration = "$selectedMonth/$selectedYear",
                    customer = useCode!!,
                    shortCode = binding.shortCodeTet.text.toString(),
                    code = accountCode,
                    isMerchant = "0"
                )
            )

            if (valid) {
                if (binding.isMobileAccount.isChecked) {
                    viewModel.editPaymentMethod(
                        AddPaymentMethodRequest(
                            operator = operatorCode,
                            type = accountType,
                            card = binding.cardNumberTet.text.toString(),
                            cardName = binding.cardNameTet.text.toString(),
                            expiration = "$selectedMonth/$selectedYear",
                            customer = useCode,
                            shortCode = binding.shortCodeTet.text.toString(),
                            code = accountCode,
                            isMerchant = "0"
                        )
                    )
                } else {
                    viewModel.editPaymentMethod(
                        AddPaymentMethodRequest(
                            operator = operatorCode,
                            type = accountType,
                            card = binding.cardNumberTet.text.toString(),
                            cardName = binding.cardNameTet.text.toString(),
                            expiration = "$selectedMonth/$selectedYear",
                            customer = useCode,
                            shortCode = binding.shortCodeTet.text.toString(),
                            code = accountCode,
                            isMerchant = "0"
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
            selectedMonth = args.account.expiration?.split('/')?.get(0)!!
            selectedYear = args.account.expiration?.split('/')?.get(1)!!
        }
    }

    private fun observers() {
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

        viewModel.providers.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (element in it) {
                    items.add(element.name!!)
                }
            }
        }

        viewModel.providers.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (element in it) {
                    cardItems.add(element.name!!)
                }
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

        viewModel.isPhoneNumberEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.phoneNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.phoneNumberTil.error = null
            }
        }

        viewModel.isCardNameValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardNameTil.error = null
            } else {
                binding.cardNameTil.error = "Ce champ est obligatoir"
            }
        }

        viewModel.isCardNumberEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardNumberTil.error = "Ce champ est obligatoir"
            } else {
                binding.cardNumberTil.error = null
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

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(EditPaymentAccountFragmentDirections.actionEditPaymentAccountFragmentToAccountsFragment())
                viewModel.navigateToHomeDone()
            }
        }

        viewModel.response.observe(viewLifecycleOwner) {
            if (it.status != "0") {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}