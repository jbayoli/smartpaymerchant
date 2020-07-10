package cd.shuri.smaprtpay.merchant.screens.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.databinding.FragmentSingInBinding
import cd.shuri.smaprtpay.merchant.network.LoginRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 */
class SingInFragment : Fragment() {

    private lateinit var binding: FragmentSingInBinding

    private val viewModel by viewModels<SignInViewModel>()

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSingInBinding.inflate(layoutInflater)

        //Hide action bar for sign in fragment
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()

        //Handle back button
        val callBack =requireActivity().onBackPressedDispatcher.addCallback(this) {
            Timber.d("Back button pressed")
            requireActivity().finish()
            exitProcess(0)
        }
        callBack.isEnabled = true

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToAccountFragment())
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val valid = viewModel.validateForm(LoginRequest(
            binding.userNameTet.text.toString(),
            binding.passwordTet.text.toString(),
            "mc")
        )

        if (valid) {
            viewModel.login(LoginRequest(
                binding.userNameTet.text.toString(),
                binding.passwordTet.text.toString(),
                "mc")
            )
        } else {
            return
        }
    }

    private fun observers() {
        viewModel.isPasswordEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.passwordTil.error = "Ce champ est obligatoire"
            } else {
                binding.passwordTil.error = null
            }
        })

        viewModel.isUserNameEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.userNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.userNameTil.error = null
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

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it == 1) {
                    Toast.makeText(requireContext(), "Nom d'utilisateur ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                }
                viewModel.showToastDone()
            }
        })

    }

}
