package com.example.kame_way.providers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import android.graphics.PointF
import com.example.kame_way.MainActivity
import com.example.kame_way.shared.PoseLandmarkMessenger

class PoseProcessor(private val context: Context) {
    private val poseLandmarker: PoseLandmarker
    private var bitmap: Bitmap? = null
    
    @Volatile
    private var isProcessing = false

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("pose/pose_landmarker_lite.task")
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result: PoseLandmarkerResult, _: MPImage -> 
                val count = result.landmarks().size
                Log.d("PoseProcessor", "gotta detect")
                Log.d("Context", "Context: $context")
                val landmarks = result.landmarks().flatMap { poses ->
                    poses.map { PointF(it.x(), it.y()) }
                }

                PoseLandmarkMessenger.sendLandmarks(
                    landmarks.map { it.x to it.y }
                )
            }
            .setErrorListener { e: RuntimeException ->
                Log.e("PoseProcessor", "Error during pose detection", e)
            }
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    fun process(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }
    
        isProcessing = true

        val frameBitmap = this.imageProxyToBitMap(imageProxy)

        val mpImage: MPImage = BitmapImageBuilder(frameBitmap)
            .build()

        val timestampMicros = imageProxy.imageInfo.timestamp

        try {
            poseLandmarker.detectAsync(mpImage, timestampMicros)
        } catch (e: Exception) {
            Log.e("PoseProcessor", "Failed to process frame", e)
        }finally {
            isProcessing = false
        }
    }

    fun imageProxyToBitMap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer
    
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
    
        val nv21 = ByteArray(ySize + uSize + vSize)
    
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
    
        val yuvImage = android.graphics.YuvImage(
            nv21,
            android.graphics.ImageFormat.NV21,
            imageProxy.width,
            imageProxy.height,
            null
        )
    
        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height),
            100,
            out
        )
    
        val jpegBytes = out.toByteArray()
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    
        imageProxy.close()
    
        // Rotaciona se necessário
        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        }
    
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
}
