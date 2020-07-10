package cd.shuri.smaprtpay.merchant.screens.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentHomeBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    private val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        setHasOptionsMenu(true)

        observers()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.signUpFirstFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSignUpFirstFragment())
                true
            }
            R.id.accountsFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountsFragment())
                true
            }
            R.id.addAccountFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddAccountFragment())
                true
            }
            R.id.singInFragment -> {
                deleteSharedPreferences()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSingInFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.transactionsCv.setOnClickListener {
            if(viewModel.response.value!!.all!! > 0) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsFragment())
            }else {
                Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
            }
        }

        binding.transactionsDoneCv.setOnClickListener {
            if (viewModel.response.value!!.clos!! > 0) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsValidatedFragment())
            } else {
                Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
            }
        }

        binding.waitingTransactionsCv.setOnClickListener {
            if (viewModel.response.value!!.waiting!! > 0) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionValidation())
            } else {
                Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteSharedPreferences() {
        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.clear()
        preferencesEditor.apply()
    }

    private fun observers() {
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

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.allTv.text = "Transactions(${it.all})"
                binding.waitingTv.text = "En attente(${it.waiting})"
                binding.doneTv.text = "Clos(${it.clos})"
            }
        })
    }
}
