package cd.shuri.smaprtpay.merchant.screens.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cd.shuri.smaprtpay.merchant.databinding.HelpFragmentBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

class HelpFragment : Fragment() {

    private lateinit var binding : HelpFragmentBinding

    private val viewModel by viewModels<HelpViewModel>()

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HelpFragmentBinding.inflate(layoutInflater)

        observers()

        return binding.root
    }

    private fun observers() {
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

        viewModel.help.observe(viewLifecycleOwner, {
            binding.helpText.text = it.helpText
            binding.helpPhone.text = "Téléphone: ${it.helpPhoneNumber}"
            binding.helpMail.text = "Email: ${it.helpEmail}"
        })
    }

}