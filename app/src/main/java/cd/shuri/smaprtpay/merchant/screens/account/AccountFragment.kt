package cd.shuri.smaprtpay.merchant.screens.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.databinding.FragmentAccountBinding

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    private val viewModel by viewModels<AccountViewModel>()

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
            viewModel.setPhoneNumber(binding.phoneNumberTet.text.toString())
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
    }

}
