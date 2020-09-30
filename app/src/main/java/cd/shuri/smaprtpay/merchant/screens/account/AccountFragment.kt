package cd.shuri.smaprtpay.merchant.screens.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.FragmentAccountBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    private val viewModel by viewModels<AccountViewModel>()
    val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        observers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.validateButton.setOnClickListener {
            if (binding.checkBox.isChecked) {
                viewModel.setCountryCode(binding.countryCodeTet.text.toString())
                viewModel.setPhoneNumber(binding.phoneNumberTet.text.toString())
            } else {
                val builder = MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_AppTheme_Dialog
                )
                    .setMessage("Veuillez accepter les termes pour continuer à utiliser FlexPay")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            }
        }
    }

    private fun observers() {

        viewModel.navigateToValidationFragment.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(AccountFragmentDirections
                    .actionAccountFragmentToValidationFragment("${binding.countryCodeTet.text.toString()
                        .removePrefix("+")}${binding.phoneNumberTet.text.toString()}"))
                viewModel.navigateToValidationFragmentDone()
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

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (it.status == "2") {
                binding.textInputLayout.error = "Ce numéro est utilisé par un autre compte"
            } else if (it.status == "1") {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}
