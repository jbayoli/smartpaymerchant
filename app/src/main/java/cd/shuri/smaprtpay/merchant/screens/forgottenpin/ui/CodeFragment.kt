package cd.shuri.smaprtpay.merchant.screens.forgottenpin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentCodeBinding
import cd.shuri.smaprtpay.merchant.screens.forgottenpin.viewmodel.ForgottenViewModel


/**
 * A simple [Fragment] subclass.
 */
class CodeFragment : Fragment() {

    private lateinit var binding: FragmentCodeBinding

    private val viewModel by viewModels<ForgottenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCodeBinding.inflate(layoutInflater)
        observers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nextButton.setOnClickListener {
            val isFieldValid = viewModel.validateForm(binding.userCodeTet.text.toString())
            if (isFieldValid) {
                viewModel.sendRequest(binding.userCodeTet.text.toString(), "step2")
            }
        }
    }

    private fun observers() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    SmartPayApp.dialogLoader.show(
                        requireActivity().supportFragmentManager,
                        "loader"
                    )
                } else {
                    SmartPayApp.dialogLoader.dismiss()
                }
                viewModel.showDialogDone()
            }
        }

        viewModel.navigateTo.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    CodeFragmentDirections.actionCodeFragmentToRenewPasswordFragment(
                        true,
                        viewModel.response.value?.user!!
                    )
                )
                viewModel.navigateToDone()
            }
        }

        viewModel.isCodeEmpty.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    binding.userCodeTil.error = "Code incorrect"
                } else {
                    binding.userCodeTil.error = null
                }
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastDone()
            }
        }

        viewModel.isCodeCorrect.observe(viewLifecycleOwner) { code ->
            code?.let {
                if (it) {
                    binding.userCodeTil.error = null
                } else {
                    binding.userCodeTil.error = "Code incorrect"
                }
                viewModel.reinitCode()
            }
        }
    }
}