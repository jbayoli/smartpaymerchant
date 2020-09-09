package cd.shuri.smaprtpay.merchant.screens.signupsecond

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp

import cd.shuri.smaprtpay.merchant.databinding.FragmentSignUpSecondBinding
import cd.shuri.smaprtpay.merchant.network.RegisterRequest
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SignUpSecondFragment : Fragment() {

    private lateinit var binding: FragmentSignUpSecondBinding

    private val viewModel by viewModels<SignUpSecondViewModel>()

    private lateinit var args: SignUpSecondFragmentArgs

    private val dialog = LoaderDialog()

    private val userCode = SmartPayApp.preferences.getString("user_code", "")

    private val items = mutableListOf<String>()

    private var sector = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args = SignUpSecondFragmentArgs.fromBundle(requireArguments())

        Timber.d("Phone1 : ${args.phone1} Phone2: ${args.phone2}")

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
        val valid = viewModel.validateForm(RegisterRequest(
            args.lastName,
            args.firstName,
            args.email,
            args.phone1,
            args.phone2,
            args.address,
            binding.activityTet.text.toString(),
            sector,
            binding.rccmTet.text.toString(),
            binding.nifTet.text.toString(),
            binding.ownerIdTet.text.toString(),
            userCode!!
        ))
        if (valid) {
            viewModel.register(RegisterRequest(
                args.lastName,
                args.firstName,
                args.email,
                args.phone1,
                args.phone2,
                args.address,
                binding.activityTet.text.toString(),
                sector,
                binding.rccmTet.text.toString(),
                binding.nifTet.text.toString(),
                binding.ownerIdTet.text.toString(),
                userCode
            ))
        }
    }

    private fun observers() {

        viewModel.isActivityEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.activityTil.error = "Ce champ est obligatoire"
            } else {
                binding.activityTil.error = null
            }
        })

        viewModel.isSectorSelected.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.sectorTil.error = null
            } else {
                binding.sectorTil.error = "Sélectionner un secteur"
            }
        })

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

        viewModel.showToastSuccess.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), viewModel.messageRegister.value, Toast.LENGTH_SHORT).show()
                viewModel.showToastSuccessDone()
            }
        })

        viewModel.showToastError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), viewModel.messageRegister.value, Toast.LENGTH_SHORT).show()
                viewModel.showToastErrorDone()
            }
        })

        viewModel.sectors.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items.add(element.name!!)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
                (binding.sectorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(SignUpSecondFragmentDirections.actionSignUpSecondFragmentToAddFirstPaymentAccountFragment())
                viewModel.navigateToHomeDone()
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone2()
            }
        })

    }

}
