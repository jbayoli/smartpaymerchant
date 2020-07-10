package cd.shuri.smaprtpay.merchant.utilities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.DagiloAddCompanyBinding
import cd.shuri.smaprtpay.merchant.screens.signup.SignUpViewModel

class AddCompanyDialog(private val viewModel: SignUpViewModel): DialogFragment() {
    private lateinit var binding: DagiloAddCompanyBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            binding = DagiloAddCompanyBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, R.style.ThemeOverlay_AppTheme_Dialog)
            builder.setView(binding.root).
            setPositiveButton("Ajouter") { _, _ ->
                viewModel.navigate(true)
            }.setNegativeButton("Plus tard") { _, _ ->
                viewModel.navigate(false)
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}