package cd.shuri.smaprtpay.merchant.screens.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cd.shuri.smaprtpay.merchant.databinding.NotificationsFragmentBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

class NotificationsFragment : Fragment() {

    private val viewModel by viewModels<NotificationsViewModel>()
    private lateinit var binding : NotificationsFragmentBinding
    private val dialogLoader =  LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NotificationsFragmentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.notificationRecyclerView.adapter = NotificationsAdapter()

        observers()
        return binding.root
    }

    private fun observers() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {
                    dialogLoader.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialogLoader.dismiss()
                }
            }
        }

        viewModel.showTToastForError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        }

        viewModel.notifications.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noNotification.visibility = View.VISIBLE
            } else {
                binding.noNotification.visibility = View.GONE
            }
        }
    }

}