package cd.shuri.smaprtpay.merchant.screens.ticket

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cd.shuri.smaprtpay.merchant.R
import cd.shuri.smaprtpay.merchant.databinding.SearchTicketFragmentBinding
import cd.shuri.smaprtpay.merchant.network.TicketVerification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SearchTicketFragment : Fragment() {

    private lateinit var binding: SearchTicketFragmentBinding
    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private lateinit var viewModel: SearchTicketViewModel
    private var isCheckingTicket: Boolean? = null

    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { binding.previewView.display.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchTicketFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SearchTicketViewModel::class.java)

        showDialog()

        setupCamera()
        observeOnTicketVerificationResult()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener {
            binding.message.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
            binding.info.visibility = View.GONE
            binding.messageTv.text = "Ticket encoure de verification"
            binding.closeButton.visibility = View.GONE
            binding.imageStatus.visibility = View.GONE
            isCheckingTicket = null
            showDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                Timber.d("no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Verification ticket")
            .setMessage("Souhaiter vous verifier la validité de votre ticket ou accèder à l'activité")
            .setNegativeButton("Verifier") { _, _ ->
                isCheckingTicket = false
                viewModel.reinitOpen()
            }
            .setPositiveButton("Accèder") { _, _ ->
                isCheckingTicket = true
                viewModel.reinitOpen()
            }
            .setNeutralButton("Annuler") { _, _ ->
                findNavController().navigate(SearchTicketFragmentDirections.actionSearchTicketFragmentToHomeFragment())
            }
            .show()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity().baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        viewModel.processCameraProvider
            .observe(viewLifecycleOwner) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                if (isCameraPermissionGranted()) {
                    bindCameraUseCases()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_CODE
                    )
                }
            }

        viewModel.isOpen.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    isCheckingTicket?.let { _ ->
                        binding.message.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.ticketScanned.observe(viewLifecycleOwner) {
            it?.let {value ->
                isCheckingTicket?.let {checking ->
                    binding.imageAndProgress.visibility = View.VISIBLE
                    viewModel.verifyTicket(TicketVerification(value, checking))
                }
            }
        }
    }

    private fun observeOnTicketVerificationResult() {
        viewModel.ticketVerificationResult.observe(viewLifecycleOwner) {
            if (it.status == "0") {
                binding.progress.visibility = View.GONE
                binding.messageTv.text = it.message
                binding.closeButton.visibility = View.VISIBLE
                binding.imageStatus.visibility = View.VISIBLE
                binding.info.visibility = View.VISIBLE
                if (!it.libelle.isNullOrEmpty()) {
                    binding.libelleValue.text = it.libelle
                    binding.dateUsedValue.text = it.clearedAt
                    binding.dateValue.text = it.purchaseAt
                }
                binding.imageStatus.setImageResource(R.drawable.ic_round_check_circle_24)
            }else {
                binding.progress.visibility = View.GONE
                binding.info.visibility = View.GONE
                binding.messageTv.text = it.message
                binding.closeButton.visibility = View.VISIBLE
                binding.imageStatus.visibility = View.VISIBLE
                binding.imageStatus.setImageResource(R.drawable.ic_round_remove_circle_24)
            }
        }

        viewModel.showTToastForError.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = View.GONE
                binding.info.visibility = View.GONE
                binding.messageTv.text = "Impossible de contacter le serveur, verifier votre connection ou essayer plus tard"
                binding.closeButton.visibility = View.VISIBLE
                binding.imageStatus.visibility = View.VISIBLE
                binding.imageStatus.setImageResource(R.drawable.ic_round_remove_circle_24)
                viewModel.showToastErrorDone()
            }
        }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) return

        if (previewUseCase != null) cameraProvider!!.unbind(previewUseCase)

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        previewUseCase!!.setSurfaceProvider(binding.previewView.surfaceProvider)

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Timber.e(illegalStateException)
        } catch (illegalArgumentException: IllegalFormatCodePointException) {
            Timber.e(illegalArgumentException)
        }
    }

    private fun bindAnalyseUseCase() {
        val barcodeOption = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(barcodeOption)

        if (cameraProvider == null) return

        if (analysisUseCase != null) cameraProvider!!.unbind(analysisUseCase)

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(
            cameraExecutor,
            { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Timber.e(illegalStateException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Timber.e(illegalArgumentException)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(
            imageProxy.image!!,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    it.rawValue?.let { value ->
                        viewModel.setOpen(value)
                    }
                }
            }
            .addOnFailureListener {
                Timber.e(it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    companion object {
        private const val REQUEST_CAMERA_CODE = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}