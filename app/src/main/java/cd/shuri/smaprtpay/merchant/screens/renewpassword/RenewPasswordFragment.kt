package cd.shuri.smaprtpay.merchant.screens.renewpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentRenewPasswordBinding
import cd.shuri.smaprtpay.merchant.network.EditPasswordRequestData
import cd.shuri.smaprtpay.merchant.network.ForgottenPINRequestThree
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog


/**
 * A simple [Fragment] subclass.
 * Use the [RenewPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RenewPasswordFragment : Fragment() {

    private lateinit var binding: FragmentRenewPasswordBinding

    private val viewModel by viewModels<RenewPasswordViewModel>()

    private val userCode = SmartPayApp.preferences.getString("user_code", "")

    private lateinit var args: RenewPasswordFragmentArgs

    val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args = RenewPasswordFragmentArgs.fromBundle(requireArguments())
        binding = FragmentRenewPasswordBinding.inflate(layoutInflater)
        if (args.isPINForgotten) {
            binding.confirmPasswordTil.visibility = View.GONE
            setHint("Nouveau PIN FlexPay", "Confirmer PIN FlexPay")
        } else {
            setHint("Ancien PIN FlexPay", "Nouveau PIN FlexPay")
        }
        observers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.validateButton.setOnClickListener {
            changePassword()
        }
    }

    private fun setHint(textOne: String, textTwo: String) {
        binding.fieldOneText = textOne
        binding.fieldTwoText = textTwo
    }

    private fun changePassword() {
        val valid = if (args.isPINForgotten) {
            viewModel.validateForm(
                EditPasswordRequestData(
                    binding.oldPasswordTet.text.toString(),
                    binding.newPasswordTet.text.toString(),
                    userCode!!
                ),
                true
            )
        } else {
            viewModel.validateForm(
                EditPasswordRequestData(
                    binding.oldPasswordTet.text.toString(),
                    binding.newPasswordTet.text.toString(),
                    userCode!!
                )
            )
        }

        if (valid) {
            if (args.isPINForgotten) {
                viewModel.newPinRequest(
                    ForgottenPINRequestThree(
                        args.user,
                        binding.oldPasswordTet.text.toString(),
                        binding.newPasswordTet.text.toString()
                    )
                )
            } else {
                val new = binding.newPasswordTet.text.toString()
                val confirm = binding.confirmPasswordTet.text.toString()
                if (new == confirm) {
                    binding.confirmPasswordTil.error = null
                    viewModel.changePassword(
                        EditPasswordRequestData(
                            binding.oldPasswordTet.text.toString(),
                            binding.newPasswordTet.text.toString(),
                            userCode
                        )
                    )
                } else {
                    binding.confirmPasswordTil.error = "PIN invalide"
                }
            }
        } else {
            return
        }
    }

    private fun observers() {
        viewModel.isOldPasswordEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.oldPasswordTil.error = "PIN vide"
                binding.oldPasswordTil.isErrorEnabled = true
            } else {
                binding.oldPasswordTil.error = null
            }
        })

        viewModel.isNewPasswordEmpty.observe(viewLifecycleOwner, {
            if (it) {
                binding.newPasswordTil.error = "PIN vide"
                binding.newPasswordTil.isErrorEnabled = true
            } else {
                binding.newPasswordTil.error = null
            }
        })

        viewModel.isPasswordMatches.observe(viewLifecycleOwner, {
            if (it) {
                if (args.isPINForgotten) {
                    binding.newPasswordTil.error = null
                } else {
                    binding.newPasswordTil.error = "Le nouveau PIN doit être different de l'ancien"
                }
            } else {
                if (args.isPINForgotten) {
                    binding.newPasswordTil.error = "Les PINs ne doivent pas être different"
                } else {
                    binding.newPasswordTil.error = null
                }
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

        viewModel.responseStatus.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == "0") {
                    binding.oldPasswordTil.error = null
                    Toast.makeText(
                        requireContext(),
                        "${viewModel.responseData.value!!.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.oldPasswordTil.error = "L'ancien PIN saisi est incorrect"
                }
                viewModel.showToastDone()
            }
        })

        viewModel.navigateToSignIn.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(RenewPasswordFragmentDirections.actionRenewPasswordFragmentToSingInFragment())
                viewModel.navigateToSignInDone()
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

        viewModel.showMessage.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.showMessageDone()
            }
        })
    }

}