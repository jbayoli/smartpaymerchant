package cd.shuri.smaprtpay.merchant.screens.signupsecond

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentSignUpSecondBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SignUpSecondFragment : Fragment() {

    private lateinit var binding: FragmentSignUpSecondBinding

    private val viewModel by viewModels<SignUpSecondViewModel>()

    private val args: SignUpSecondFragmentArgs by navArgs()

    private val dialog = LoaderDialog()

    private val userCode = SmartPayApp.preferences.getString("user_code", "")

    private val items = mutableListOf<String>()

    private var sector = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSignUpSecondBinding.inflate(layoutInflater)
        observers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.validateButton.setOnClickListener {
            register()
        }

        binding.sectorAuto.setOnItemClickListener { adapterView, _, i, _ ->
            Timber.d("user select ${adapterView.getItemAtPosition(i)}")
            for (element in viewModel.sectors.value!!) {
                if (element.name == adapterView.getItemAtPosition(i)) {
                    sector = element.code!!
                }
            }
        }
    }

    private fun register() {
        val request = args.registerData
        request.activity = binding.activityTet.text.toString()
        request.sector = sector
        request.rccm = binding.rccmTet.text.toString()
        request.nif = binding.nifTet.text.toString()
        request.owner = binding.ownerIdTet.text.toString()
        request.customer = userCode!!
        val valid = viewModel.validateForm(
            request
        )
        if (valid) {
            viewModel.register(
                request
            )
        }
    }

    private fun observers() {

        viewModel.isActivityEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.activityTil.error = "Ce champ est obligatoire"
            } else {
                binding.activityTil.error = null
            }
        }

        viewModel.isSectorSelected.observe(viewLifecycleOwner) {
            if (it) {
                binding.sectorTil.error = null
            } else {
                binding.sectorTil.error = "Sélectionner un secteur"
            }
        }

        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }

        viewModel.showToastSuccess.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    viewModel.messageRegister.value,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastSuccessDone()
            }
        }

        viewModel.showToastError.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    viewModel.messageRegister.value,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        }

        viewModel.sectors.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
                (binding.sectorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    SignUpSecondFragmentDirections.actionSignUpSecondFragmentToAddFirstPaymentAccountFragment(
                        args.phoneNumber
                    )
                )
                viewModel.navigateToHomeDone()
            }
        }

        viewModel.showTToastForError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone2()
            }
        }

    }

}
