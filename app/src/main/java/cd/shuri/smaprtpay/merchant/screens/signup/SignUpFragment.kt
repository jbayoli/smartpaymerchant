package cd.shuri.smaprtpay.merchant.screens.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.databinding.FragmentSignUpBinding
import cd.shuri.smaprtpay.merchant.network.SingUpRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel by viewModels<SignUpViewModel>()

    private lateinit var args: SignUpFragmentArgs

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        args = SignUpFragmentArgs.fromBundle(requireArguments())

        binding = FragmentSignUpBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.validateButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val valid = viewModel.validateForm(
            SingUpRequest(
                binding.merchantCodeTet.text.toString(),
                binding.userNameTet.text.toString(),
                binding.passwordTet.text.toString(),
                binding.passwordConfirmTet.text.toString(),
                args.phoneNumber,
                args.fcmToken
            )
        )
        if (valid) {
            viewModel.signUp(
                SingUpRequest(
                    binding.merchantCodeTet.text.toString(),
                    binding.userNameTet.text.toString(),
                    binding.passwordTet.text.toString(),
                    binding.passwordConfirmTet.text.toString(),
                    args.phoneNumber,
                    args.fcmToken
                )
            )
        } else {
            return
        }
    }

    private fun observers() {

        viewModel.isCodeEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.merchantCodeTil.error = "Ce champ est obligatoire"
            } else {
                binding.merchantCodeTil.error = null
            }
        })

        viewModel.isUserNameEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.userNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.userNameTil.error = null
            }
        })

        viewModel.isPasswordEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.passwordTil.error = "Ce champ est obligatoire"
            } else {
                binding.passwordTil.error = null
            }
        })

        viewModel.isConfirmedPasswordEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.passwordConfirmTil.error = "Ce champ est obligatoire"
            } else {
                binding.passwordConfirmTil.error = null
            }
        })

        viewModel.isPasswordsMatches.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.passwordConfirmTil.error = null
            } else {
                binding.passwordConfirmTil.error = "Les PINS sont différents"
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

        viewModel.navigateTo.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpFirstFragment())
                viewModel.navigationDone()
            }
        })

        viewModel.isUserNameExist.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.userNameTil.error = "Code utilisateur existe déjà"
            } else {
                binding.userNameTil.error = null
            }
        })

        viewModel.isMerchantCodeExist.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.merchantCodeTil.error = "Le code marchand existe déjà"
            } else {
                binding.merchantCodeTil.error = null
            }
        })
    }

}
