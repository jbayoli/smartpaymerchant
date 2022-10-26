package cd.shuri.smaprtpay.merchant.screens.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.SmartPayApp
import cd.shuri.smaprtpay.merchant.databinding.FragmentHomeBinding
import cd.shuri.smaprtpay.merchant.utilities.LoaderDialog
import cd.shuri.smaprtpay.merchant.utilities.generateQRCode

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
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.name = name

        (requireActivity() as AppCompatActivity).supportActionBar!!.show()

        createChannel(getString(R.string.notification_chanel_id), "Paiement")
        name?.let {
            val bitmap = generateQRCode(it, requireContext())
            binding.qrCodeImage.setImageBitmap(bitmap)
        }

        observers()

        createMenu()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.transactionsButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.all!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsFragment())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aucune transaction disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.waitButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.validatings!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionValidation())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aucune transaction disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.waitTButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.waiting!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionInWaitFragment())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aucune transaction disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.errorButton.setOnClickListener {
            viewModel.response.value?.let {
                if (it.errors!! > 0) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionsErrorFragment())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aucune transaction disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.newTransferButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDoTransferFragment2())
        }

        binding.qrCode.setOnClickListener {
            name?.let {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToQRCodeFragment(
                        it
                    )
                )
            }
        }

        binding.method.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountsFragment())
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

        binding.ticketButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchTicketFragment())
        }

        binding.validateTicket.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchTicketFragment())
        }

        onPaymentClicked()
    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.home_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.singInFragment -> {
                            deleteSharedPreferences()
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSingInFragment())
                            true
                        }

                        R.id.refresh -> {
                            viewModel.getDashBoardData()
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun onPaymentClicked() {
        binding.pay.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections
                .actionHomeFragmentToPaymentFragment()
            )
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
        viewModel.showDialogLoader.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    dialog.show(requireActivity().supportFragmentManager, "LoaderDialog")
                } else {
                    dialog.dismiss()
                }
                viewModel.showDialogLoaderDone()
            }
        }

        viewModel.response.observe(viewLifecycleOwner) {
            it?.let {
                binding.numberOfTransaction.text = bindNumberOfTransaction(it.all!!)
                binding.numberOfWait.text = bindNumberOfTransaction(it.validatings!!)
                binding.numberOfError.text = bindNumberOfTransaction(it.errors!!)
                binding.numberOfWaitT.text = bindNumberOfTransaction(it.waiting!!)
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
    }

    private fun bindNumberOfTransaction(number: Int): String {
        return if (number in 1000..999_999) {
            val numberString = number.toString()
            if (numberString[1] != '0') {
                "${numberString.first()}.${numberString[1]}K"
            } else {
                "${numberString.first()}K"
            }
        } else if (number in 1_000_000..999_999_999) {
            val numberString = number.toString()
            if (numberString[1] != '0') {
                "${numberString.first()}.${numberString[1]}M"
            } else {
                "${numberString.first()}M"
            }
        } else {
            "$number"
        }
    }
}
