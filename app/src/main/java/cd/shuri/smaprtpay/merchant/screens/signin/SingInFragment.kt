package cd.shuri.smaprtpay.merchant.screens.signin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentSingInBinding
import cd.shuri.smaprtpay.merchant.network.LoginRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 */
class SingInFragment : Fragment() {

    private lateinit var binding: FragmentSingInBinding

    private val viewModel by viewModels<SignInViewModel>()

    private val args: SingInFragmentArgs by navArgs()

    private val name = SmartPayApp.preferences.getString("username", "")

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSingInBinding.inflate(layoutInflater)

        if (!name.isNullOrEmpty()) {
            binding.userNameTet.setText(name)
        }

        showDialogs()

        //Hide action bar for sign in fragment
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()

        //Handle back button
        val callBack = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Timber.d("Back button pressed")
            requireActivity().finish()
            exitProcess(0)
        }
        callBack.isEnabled = true

        binding.version.text = getString(R.string.version)
        binding.version.setTextColor(Color.RED)

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

        binding.forgetPasseWordButton.setOnClickListener {
            findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToUserCodeFragment())
        }

        binding.helpButton.setOnClickListener {
            findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToHelpFragment())
        }
    }

    private fun signIn() {
        val valid = viewModel.validateForm(
            LoginRequest(
                binding.userNameTet.text.toString(),
                binding.passwordTet.text.toString(),
                "mc",
                viewModel.token
            )
        )

        if (valid) {
            viewModel.login(
                LoginRequest(
                    binding.userNameTet.text.toString(),
                    binding.passwordTet.text.toString(),
                    "mc",
                    viewModel.token
                )
            )
        } else {
            return
        }
    }

    private fun showDialogs(step: Int = 0) {
        if (args.showDialogForRegister || step == 3) {
            val builder =
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                    .setMessage("Vous devez terminer le processus d'inscription sinon le processus complet sera annulé")
                    .setPositiveButton("Continuer") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToSignUpFirstFragment())
                    }
                    .setNegativeButton("Annuler") { dialog, _ ->
                        viewModel.deleteUser()
                        dialog.dismiss()
                    }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }

        if (args.shshowDialogForAccount || step == 4) {
            val builder =
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_AppTheme_Dialog)
                    .setMessage("Vous devez terminer le processus d'inscription sinon le processus complet sera annulé")
                    .setPositiveButton("Continuer") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToAddFirstPaymentAccountFragment())
                    }
                    .setNegativeButton("Annuler") { dialog, _ ->
                        viewModel.deleteUser()
                        dialog.dismiss()
                    }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun observers() {
        viewModel.isPasswordEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.passwordTil.error = "Ce champ est obligatoire"
            } else {
                binding.passwordTil.error = null
            }
        })

        viewModel.isUserNameEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.userNameTil.error = "Ce champ est obligatoire"
            } else {
                binding.userNameTil.error = null
            }
        })

        viewModel.showDialogLoader.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, {
            if (it != null) {
                findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToHomeFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.step.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    3 -> {
                        showDialogs(it)
                    }
                    4 -> {
                        showDialogs(it)
                    }
                    else -> {
                        viewModel.signIn()
                    }
                }
            }
        })

        viewModel.loginStatus.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == 1) {
                    Toast.makeText(
                        requireContext(),
                        "Nom d'utilisateur ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.showToastDone()
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        })

        viewModel.response.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                if (it.status == "0") {
                    val preferencesEditor = SmartPayApp.preferences.edit()
                    preferencesEditor.clear()
                    preferencesEditor.apply()
                }
            }
        })

    }

}
