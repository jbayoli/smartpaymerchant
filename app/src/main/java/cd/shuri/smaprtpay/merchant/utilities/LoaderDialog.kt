package cd.shuri.smaprtpay.merchant.utilities

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.LoaderDialogBinding

class LoaderDialog: DialogFragment() {
    private lateinit var binding: LoaderDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = LoaderDialogBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.ThemeOverlay_AppTheme_Dialog).setView(binding.root).create()
            builder.setCanceledOnTouchOutside(false)
            builder
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}