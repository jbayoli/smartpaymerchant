package cd.shuri.smaprtpay.merchant.screens.signupfirst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.SmartPayApp

import cd.shuri.smaprtpay.merchant.databinding.FragmentSignUpFirstBinding

/**
 * A simple [Fragment] subclass.
 */
class SignUpFirstFragment : Fragment() {

    private lateinit var binding: FragmentSignUpFirstBinding

    private val viewModel by viewModels<SignUpFirstViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.putString("isRegistrationDone", "false")
        preferencesEditor.apply()

        binding = FragmentSignUpFirstBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            navigate()
        }
    }

    private fun navigate() {
        val valid = viewModel.validateFrom(
            binding.lastNameTet.text.toString(),
            binding.firstNameTet.text.toString(),
            binding.mailTet.text.toString(),
            binding.phoneNumberOneTet.text.toString(),
            binding.phoneNumberTwoTet.text.toString(),
            binding.addressTet.text.toString()
        )

        if (valid) {
            viewModel.setNavigateToHome(true)
        } else {
            return
        }
    }

    private fun observers() {
        viewModel.isLastNameEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.lastNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.lastNameTil.error = null
            }
        })

        viewModel.isFirstNameEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.firstNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.firstNameTil.error = null
            }
        })

        viewModel.isEmailEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mailTil.error = "Ce champ est obligatoire"
            } else {
                binding.mailTil.error = null
            }
        })

        viewModel.isAddressEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.addressTil.error = "Ce champ est obligatoire"
            } else {
                binding.addressTil.error = null
            }
        })

        viewModel.isPhoneNumber1Empty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberOneTil.error = "Ce champ est obligatoire"
            } else {
                binding.phoneNumberOneTil.error = null
            }
        })

        viewModel.isEmailValid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mailTil.error = null
            } else {
                binding.mailTil.error = "Email invalide"
            }
        })

        viewModel.navigateToSignUp2.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                var phone2 = ""
                if (binding.phoneNumberTwoTet.text.toString().isNotEmpty()) {
                   phone2 =  "${binding.countryCodeTetTwo.text.toString().removePrefix("+")}${binding.phoneNumberTwoTet.text.toString()}"
                }
                findNavController().navigate(SignUpFirstFragmentDirections.actionSignUpFirstFragmentToSignUpSecondFragment(
                    binding.lastNameTet.text.toString(),
                    binding.firstNameTet.text.toString(),
                    binding.mailTet.text.toString(),
                    "${binding.countryCodeTetOne.text.toString().removePrefix("+")}${binding.phoneNumberOneTet.text.toString()}",
                    phone2,
                    binding.addressTet.text.toString()
                ))
                viewModel.navigateToSignUp2Done()
            }
        })

        viewModel.isPhoneNumber1Valid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberOneTil.error = null
            } else {
                binding.phoneNumberOneTil.error = "n° de téléphone invalide"
            }
        })

        viewModel.isPhoneNumber2Valid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phoneNumberTwoTil.error = null
            } else {
                binding.phoneNumberTwoTil.error = "n° de téléphone invalide"
            }
        })
    }
}
