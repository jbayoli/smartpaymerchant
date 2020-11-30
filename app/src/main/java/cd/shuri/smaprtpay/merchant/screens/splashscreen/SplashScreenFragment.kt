package cd.shuri.smaprtpay.merchant.screens.splashscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentSplashScreenBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private val viewModel by viewModels<SplashScreenViewModel>()
    val token = SmartPayApp.preferences.getString("token", "")
    private val isRegisterDone = SmartPayApp.preferences.getString("isRegistrationDone", null)
    private val isAccountDone = SmartPayApp.preferences.getString("isAccountDone", null)
    private val step = SmartPayApp.preferences.getInt("step", 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)

        Timber.d("$isRegisterDone")
        Timber.d("$isAccountDone")

        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()

        viewModel.navigateTo.observe(viewLifecycleOwner, {
            if (it != null) {
                if (token!!.isNotEmpty()) {
                    if (isRegisterDone != null) {
                        findNavController().navigate(
                            SplashScreenFragmentDirections.actionSplashScreenFragmentToSingInFragment(
                                true
                            )
                        )
                    } else {
                        if (isAccountDone != null) {
                            findNavController().navigate(
                                SplashScreenFragmentDirections.actionSplashScreenFragmentToSingInFragment(
                                    shshowDialogForAccount = true
                                )
                            )
                        } else {
                            if (step == 3 || step == 4) {
                                findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSingInFragment())
                            } else {
                                findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment())
                            }
                        }
                    }
                } else {
                    findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSingInFragment())
                }
                viewModel.navigateToDone()
            }
        })
        return binding.root
    }

}
