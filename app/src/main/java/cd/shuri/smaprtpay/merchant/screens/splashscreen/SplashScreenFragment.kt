package cd.shuri.smaprtpay.merchant.screens.splashscreen

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
import cd.shuri.smaprtpay.merchant.databinding.FragmentSplashScreenBinding

/**
 * A simple [Fragment] subclass.
 */
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private val viewModel by viewModels<SplashScreenViewModel>()
    val token = SmartPayApp.preferences.getString("token", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()

        viewModel.navigateTo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (token!!.isNotEmpty()) {
                    findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment())
                } else {
                    findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSingInFragment())
                }
                viewModel.navigateToDone()
            }
        })
        return binding.root
    }

}
