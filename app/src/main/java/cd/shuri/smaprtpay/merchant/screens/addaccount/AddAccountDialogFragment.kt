package cd.shuri.smaprtpay.merchant.screens.addaccount

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.DialogAddAccountTypeSelectionBinding

class AddAccountDialogFragment(private val viewModel: AddAccountViewModel) : DialogFragment() {

    private  lateinit var  binding: DialogAddAccountTypeSelectionBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogAddAccountTypeSelectionBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.ThemeOverlay_AppTheme_Dialog)
            builder.setView(binding.root).
            setPositiveButton("Mobile Money") { _, _ ->
                viewModel.setAccountType(1)
            }.setNegativeButton("Carte") { _, _ ->
                viewModel.setAccountType(2)
            }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}