package cd.shuri.smaprtpay.merchant.screens.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.EditProfileFragmentBinding
import cd.shuri.smaprtpay.merchant.network.UpdateProfile
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber

class EditProfileFragment : Fragment() {

    private val viewModel by viewModels<EditProfileViewModel>()
    private lateinit var binding: EditProfileFragmentBinding
    private val args: EditProfileFragmentArgs by navArgs()
    private val usercode = SmartPayApp.preferences.getString("user_code", "")
    private var sector = ""
    private val items = mutableListOf<String>()
    private var communeSelected = ""
    private val items2 = mutableListOf<String>()
    private val itemsCode = mutableListOf<String>()
    val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditProfileFragmentBinding.inflate(layoutInflater)
        val info = args.profileInfo

        binding.firstNameTet.setText(info.firstname)
        binding.lastNameTet.setText(info.lastname)
        binding.tokenPhoneTet.setText(info.tokenPhone?.replaceFirst("243", ""))
        binding.mailTet.setText(info.email)
        binding.phoneNumberOneTet.setText(info.phone?.replaceFirst("243", ""))
        binding.phoneNumberTwoTet.setText(info.phone2?.replaceFirst("243", ""))
        binding.activityTet.setText(info.activity)
        binding.sectorAuto.setText(info.sector)
        binding.rccmTet.setText(info.rccm)
        binding.nifTet.setText(info.nif)
        binding.ownerIdTet.setText(info.ownerId)
        binding.numberTet.setText(info.number)
        binding.communeAuto.setText(info.commune)
        binding.streetTet.setText(info.street)

        observers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editButton.setOnClickListener {
            validateAndSendRequest()
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

    private fun validateAndSendRequest() {

        val firstName = if (isFieldNotEmpty(
                binding.firstNameTet.text.toString(),
                binding.firstNameTil
            )
        ) {
            binding.firstNameTet.text.toString()
        } else {
            ""
        }

        val lastName = if (isFieldNotEmpty(
                binding.lastNameTet.text.toString(),
                binding.lastNameTil
            )
        ) {
            binding.lastNameTet.text.toString()
        } else {
            ""
        }

        val tokenPhoneNumber =
            if (isFieldNotEmpty(binding.tokenPhoneTet.text.toString(), binding.tokenPhoneTil)) {
                "${
                    binding.tokenCountryCodeTet.text.toString().replaceFirst("+", "")
                }${binding.tokenPhoneTet.text.toString()}"
            } else {
                ""
            }

        val phoneNumber1 = if (isFieldNotEmpty(
                binding.phoneNumberOneTet.text.toString(),
                binding.phoneNumberOneTil
            )
        ) {
            "${
                binding.countryCodeTetOne.text.toString().replaceFirst("+", "")
            }${binding.phoneNumberOneTet.text.toString()}"
        } else {
            ""
        }

        val phoneNumber2 = if (binding.phoneNumberTwoTet.text.toString().isNotEmpty()) {
            "${
                binding.countryCodeTetTwo.text.toString().replaceFirst("+", "")
            }${binding.phoneNumberTwoTet.text.toString()}"
        } else {
            ""
        }

        val number = if (binding.numberTet.text.toString().isNotEmpty()) {
            binding.numberTet.text.toString()
        } else {
            ""
        }

        val street = if (binding.streetTet.text.toString().isNotEmpty()) {
            binding.streetTet.text.toString()
        } else {
            ""
        }

        val activity = if (isFieldNotEmpty(
                binding.activityTet.text.toString(),
                binding.activityTil
            )
        ) {
            binding.activityTet.text.toString()
        } else {
            ""
        }

        val request = UpdateProfile(
            usercode!!,
            tokenPhoneNumber,
            firstName,
            lastName,
            binding.mailTet.text.toString(),
            phoneNumber1,
            phoneNumber2,
            communeSelected,
            street,
            number,
            activity,
            binding.rccmTet.text.toString(),
            binding.nifTet.text.toString(),
            binding.ownerIdTet.text.toString(),
            sector
        )

        val validRequest =
            firstName.isNotEmpty() && lastName.isNotEmpty() && tokenPhoneNumber.isNotEmpty() && phoneNumber1.isNotEmpty() && number.isNotEmpty() && street.isNotEmpty() && activity.isNotEmpty()

        if (validRequest) {
            viewModel.updateProfile(request)
        }
    }

    private fun isFieldNotEmpty(content: String, textInputLayout: TextInputLayout): Boolean {
        return if (content.isNotEmpty()) {
            textInputLayout.error = null
            true
        } else {
            textInputLayout.error = "Ce champ est obligatoire"
            false
        }
    }

    private fun observers() {
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

        viewModel.sectors.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items.add(element.name!!)
                    if (element.name == args.profileInfo.sector) {
                        sector = element.code!!
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items)
                (binding.sectorTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })

        viewModel.navigateTo.observe(viewLifecycleOwner, {
            if (it != null) {
                findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
                viewModel.navigateToDone()
            }
        })

        viewModel.communes.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                for (element in it) {
                    Timber.d("name : ${element.name}")
                    items2.add(element.name!!)
                    itemsCode.add(element.code!!)
                }
                if (items2.contains(args.profileInfo.commune)) {
                    val index = items2.indexOf(args.profileInfo.commune)
                    communeSelected = itemsCode[index]
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_items, items2)
                (binding.communeTil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        })
    }

}