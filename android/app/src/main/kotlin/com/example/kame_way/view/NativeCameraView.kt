package com.example.kame_way.view
import android.content.Context
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.flutter.plugin.platform.PlatformView
import com.example.kame_way.providers.PoseProcessor
import java.util.concurrent.Executors
import android.os.SystemClock
import android.widget.FrameLayout

class NativeCameraView(
    context: Context, 
    private val lifecycleOwner: LifecycleOwner
    ) : PlatformView {

    private val previewView = PreviewView(context).apply {
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }
    private var camera: Camera? = null

    private val processor = PoseProcessor(context)
    
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var lastTimestamp = 0L
    private val throttleMillis = 100
    
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    private var isFrontCamera = true;

    init {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    fun toggleCamera() {
        isFrontCamera = !isFrontCamera
        cameraProviderFuture.get().let {
            bindCamera(it)
        }
    }

    fun shouldProcessFrame(): Boolean {
        val now = SystemClock.elapsedRealtime()
        return if (now - lastTimestamp >= throttleMillis) {
            lastTimestamp = now
            true
        } else false
    }

    override fun getView(): View = previewView
    override fun dispose() {
        cameraExecutor.shutdown()
    }

    private fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
    
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                    if (shouldProcessFrame()) {
                        processor.process(imageProxy)
                    } else {
                        imageProxy.close()
                    }
                })
            }
    
        val cameraSelector = if (isFrontCamera)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA
    
        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("NativeCameraView", "Failed to bind camera", e)
        }
    }

}
