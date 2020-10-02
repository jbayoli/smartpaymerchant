package cd.shuri.smaprtpay.merchant.screens.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
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
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    private val dialog = LoaderDialog()

    private val name = SmartPayApp.preferences.getString("name", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.name = name

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        setHasOptionsMenu(true)

        viewModel.getDashBoardData()

        createChannel(getString(R.string.notification_chanel_id), "Paiement")

        observers()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.singInFragment -> {
                deleteSharedPreferences()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSingInFragment())
                true
            }
            R.id.refresh -> {
                viewModel.getDashBoardData()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.transactionsButton.setOnClickListener {
            viewModel.response.value?.let {
                if(it.all!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsFragment())
                }else {
                    Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.finishedButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.clos!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsValidatedFragment())
                } else {
                    Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.waitButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.validatings!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionValidation())
                } else {
                    Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.waitTButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.waiting!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionInWaitFragment())
                } else {
                    Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.errorButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.errors!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsErrorFragment())
                } else {
                    Toast.makeText(requireContext(), "Aucune transaction disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.myTransfersButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.transferts!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransfersFragment())
                } else {
                    Toast.makeText(requireContext(), "Aucun approvisionnement disponible", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.newTransferButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDoTransferFragment2())
        }

        binding.qrCode.setOnClickListener {
            name?.let {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToQRCodeFragment(it))
            }
        }

        binding.method.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountsFragment())
        }

        binding.addMethod.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddAccountFragment())
        }

        binding.reinitPin.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRenewPasswordFragment())
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.notifications.setOnClickListener {
            findNavController().navigate((HomeFragmentDirections.actionHomeFragmentToNotificationsFragment()))
        }

        binding.helpButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHelpFragment())
        }
    }

    private fun deleteSharedPreferences() {
        val preferencesEditor = SmartPayApp.preferences.edit()
        preferencesEditor.remove("token")
        preferencesEditor.apply()
    }

    private fun createChannel(channelId: String, channelName: String) {
        // START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // Change importance
                NotificationManager.IMPORTANCE_HIGH
            )
                // Disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Paiement à valider"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
        // END create channel
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
            it?.let{
                binding.numberOfTransaction.text = bindNumberOfTransaction(it.all!!)
                binding.numberOfWait.text = bindNumberOfTransaction(it.validatings!!)
                binding.numberOfClos.text = bindNumberOfTransaction(it.clos!!)
                binding.numberOfTransfer.text = bindNumberOfTransaction(it.transferts!!)
                binding.numberOfError.text = bindNumberOfTransaction(it.errors!!)
                binding.numberOfWaitT.text = bindNumberOfTransaction(it.waiting!!)
            }
        })

        viewModel.showTToastForError.observe(viewLifecycleOwner, Observer {
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

    private fun bindNumberOfTransaction(number: Int)  : String {
        return if (number in 1000..999_999) {
            val numberString = number.toString()
            if (numberString[1] != '0' ) {
                "${numberString.first()}.${numberString[1]}K"
            } else {
                "${numberString.first()}K"
            }
        } else if (number in 1_000_000..999_999_999) {
            val numberString = number.toString()
            if (numberString[1] != '0' ) {
                "${numberString.first()}.${numberString[1]}M"
            } else {
                "${numberString.first()}M"
            }
        } else {
            "$number"
        }
    }
}
