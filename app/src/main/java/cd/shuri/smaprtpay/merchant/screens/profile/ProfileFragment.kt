package cd.shuri.smaprtpay.merchant.screens.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.FragmentProfileBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    val dialog = LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        observer()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_profileFragment_to_editProfileFragment -> {
                viewModel.response.value?.let {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(it))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }


    private fun observer() {
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
    }
}