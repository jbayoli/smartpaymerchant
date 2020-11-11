package cd.shuri.smaprtpay.merchant.screens.validation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.databinding.FragmentValidationBinding
import cd.shuri.smaprtpay.merchant.network.CodeRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import cd.shuri.smaprtpay.merchant.utilities.phoneNumberFormat

/**
 * A simple [Fragment] subclass.
 */
class ValidationFragment : Fragment() {

    private lateinit var binding: FragmentValidationBinding
    private val args: ValidationFragmentArgs by navArgs()
    private lateinit var phoneNumberFormat : String
    private lateinit var viewModel: ValidationViewModel
    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        phoneNumberFormat = phoneNumberFormat("+${args.phoneNumber}")

        val viewModelFactory = ValidationViewModelFactory(args.phoneNumber)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ValidationViewModel::class.java)
        viewModel.sendingCodeTimer()



        binding = FragmentValidationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.phoneNumber = phoneNumberFormat
        binding.viewModel = viewModel

        observers()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.timer.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.validateButton.setOnClickListener {
            viewModel.checkCodeField(binding.codeTet.text.toString())
        }

        binding.resendButton.setOnClickListener {
            viewModel.resendCode(CodeRequest(args.phoneNumber,viewModel.token))
        }
    }

    private fun observers() {

        viewModel.navigateToSignUpFragment.observe(viewLifecycleOwner, {
            if (it != null) {
                findNavController().navigate(ValidationFragmentDirections
                    .actionValidationFragmentToSignUpFragment(viewModel.token,args.phoneNumber))
                viewModel.navigateToSignUpFragmentDone()
            }
        })

        viewModel.showDialogLoader.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                }  else {
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
                viewModel.showToastErrorDone()
            }
        })
    }

}
