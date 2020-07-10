package cd.shuri.smaprtpay.merchant.utilities

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.DialogNetworkProblemBinding

class ConnectionProblemDialog: DialogFragment() {
    private lateinit var binding: DialogNetworkProblemBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogNetworkProblemBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.ThemeOverlay_AppTheme_Dialog)
            builder.setView(binding.root).
            setNeutralButton("OK"){ _, _ ->
                dismiss()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}