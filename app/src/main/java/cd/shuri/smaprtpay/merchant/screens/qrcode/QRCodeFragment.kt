package cd.shuri.smaprtpay.merchant.screens.qrcode

import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import cd.shuri.smaprtpay.merchant.databinding.FragmentQRCodeBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class QRCodeFragment : Fragment() {

    private lateinit var binding: FragmentQRCodeBinding
    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQRCodeBinding.inflate(layoutInflater)

        val args = QRCodeFragmentArgs.fromBundle(requireArguments())
        val name = args.codeText

        generateQRCode(name)

        return binding.root
    }

    private fun generateQRCode(inputValue: String) {
        val windowManager = requireActivity().getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point  = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var smallerDimension = if (width < height) {
            width
        } else {
            height
        }

        smallerDimension = smallerDimension * 3 / 4

        val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension)
        qrgEncoder.colorBlack = Color.parseColor("#003c8f")
        qrgEncoder.colorWhite = Color.parseColor("#edeff1")

        try {
            bitmap = qrgEncoder.bitmap
            binding.qRCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Timber.e("$e")
        }
    }
}