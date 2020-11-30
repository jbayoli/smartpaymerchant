package cd.shuri.smaprtpay.merchant.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cd.shuri.smaprtpay.merchant.databinding.FragmentTransfersBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog

/**
 * A simple [Fragment] subclass.
 * Use the [TransfersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransfersFragment : Fragment() {

    private lateinit var binding : FragmentTransfersBinding

    private val viewModel by viewModels<TransferViewModel>()

    private val dialogLoader =  LoaderDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransfersBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.transferRecyclerView.adapter = TransferListAdapter()

        observers()
        return binding.root
    }

    private fun observers() {
        viewModel.showDialogLoader.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    dialogLoader.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialogLoader.dismiss()
                }
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.showToastErrorDone()
            }
        })
    }
}