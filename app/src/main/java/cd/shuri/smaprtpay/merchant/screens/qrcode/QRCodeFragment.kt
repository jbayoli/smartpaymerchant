package cd.shuri.smaprtpay.merchant.screens.qrcode

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cd.shuri.smaprtpay.merchant.databinding.FragmentQRCodeBinding
import cd.shuri.smaprtpay.merchant.utilities.generateQRCode

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class QRCodeFragment : Fragment() {

    private lateinit var binding: FragmentQRCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQRCodeBinding.inflate(layoutInflater)

        val args = QRCodeFragmentArgs.fromBundle(requireArguments())
        val name = args.codeText

        binding.qRCodeText.text = args.codeText
        val bitmap = generateQRCode(args.codeText, requireContext())
        binding.qRCode.setImageBitmap(bitmap)

        return binding.root
    }
}