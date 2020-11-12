package cd.shuri.smaprtpay.merchant.screens.signupfirst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentSignUpFirstBinding
import cd.shuri.smaprtpay.merchant.network.RegisterRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SignUpFirstFragment : Fragment() {

    private lateinit var binding: FragmentSignUpFirstBinding

    private val viewModel by viewModels<SignUpFirstViewModel>()

    private val args: SignUpFirstFragmentArgs by navArgs()

    private var communeSelected = ""

    private val items = mutableListOf<String>()
    private val itemsCode = mutableListOf<String>()

    private val sexItems = listOf("Homme", "Femme")
    private val sexCodeItem = listOf("M", "F")
    private var sexSelected = ""

    val dialog = LoaderDialog()

    private lateinit var request : RegisterRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("isRegistrationDone", "false")
        preferencesEditor.apply()

        binding = FragmentSignUpFirstBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, sexItems)
        (binding.sexTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            navigate()
        }

        binding.communeAuto.setOnItemClickListener { _, _, i, _ ->
            communeSelected = itemsCode[i]
        }

        binding.sexAuto.setOnItemClickListener { _, _, position, _ ->
            sexSelected = sexCodeItem[position]
        }
    }

    private fun navigate() {

        var phone2 = ""
        if (binding.phoneNumberTwoTet.text.toString().isNotEmpty()) {
            phone2 = "${
                binding.countryCodeTetTwo.text.toString().removePrefix("+")
            }${binding.phoneNumberTwoTet.text.toString()}"
        }

        request = RegisterRequest(
            lastName= binding.lastNameTet.text.toString(),
            firstName = binding.firstNameTet.text.toString(),
            email = binding.mailTet.text.toString(),
            phone1 = "243${binding.phoneNumberOneTet.text.toString()}",
            phone2 = phone2,
            commune = communeSelected,
            number = binding.numberTet.text.toString(),
            street = binding.streetTet.text.toString(),
            sex = sexSelected
        )

        val valid = viewModel.validateFrom(
            request
        )

        if (valid) {
            if (sexSelected.isEmpty()) {
                binding.sexTil.error = "Ce champ est obligatoire"
            } else {
                binding.sexTil.error = null
                viewModel.setNavigateToHome(true)
            }
        } else {
            return
        }
    }

    private fun observers() {
        viewModel.isLastNameEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.lastNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.lastNameTil.error = null
            }
        })

        viewModel.isFirstNameEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.firstNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.firstNameTil.error = null
            }
        })

        viewModel.isEmailEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.mailTil.error = "Ce champ est obligatoire"
            } else {
                binding.mailTil.error = null
            }
        })

        viewModel.isAddressCommuneEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.communeTil.error = "Ce champ est obligatoire"
            } else {
                binding.communeTil.error = null
            }
        })

        viewModel.isAddressNumberEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.numberTil.error = "obligatoire"
            } else {
                binding.numberTil.error = null
            }
        })

        viewModel.isAddressStreetEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.streetTil.error = "Ce champ est obligatoire"
            } else {
                binding.numberTil.error = null
            }
        })

        viewModel.isPhoneNumber1Empty.observe(viewLifecycleOwner, {
            if (it) {
                binding.phoneNumberOneTil.error = "Ce champ est obligatoire"
            } else {
                binding.phoneNumberOneTil.error = null
            }
        })

        viewModel.isEmailValid.observe(viewLifecycleOwner, {
            if (it) {
                binding.mailTil.error = null
            } else {
                binding.mailTil.error = "Email invalide"
            }
        })

        viewModel.navigateToSignUp2.observe(viewLifecycleOwner, {
            if (it != null) {

                findNavController().navigate(
                    SignUpFirstFragmentDirections.actionSignUpFirstFragmentToSignUpSecondFragment(
                        request,
                        args.phoneNumber
                    )
                )
                viewModel.navigateToSignUp2Done()
            }
        })

        viewModel.isPhoneNumber1Valid.observe(viewLifecycleOwner, {
            if (it) {
                binding.phoneNumberOneTil.error = null
            } else {
                binding.phoneNumberOneTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.isPhoneNumber2Valid.observe(viewLifecycleOwner, {
            if (it) {
                binding.phoneNumberTwoTil.error = null
            } else {
                binding.phoneNumberTwoTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.showDialogLoader.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")
                } else {
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
                viewModel.showToastErrorDone2()
            }
        })

        viewModel.communes.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items.add(element.name!!)
                    itemsCode.add(element.code!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
                (binding.communeTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
    }
}
