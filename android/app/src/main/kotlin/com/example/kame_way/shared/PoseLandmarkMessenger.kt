package com.example.kame_way.shared

import android.util.Log
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel

object PoseLandmarkMessenger {
    private var eventSink: EventChannel.EventSink? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun setSink(sink: EventChannel.EventSink?) {
        eventSink = sink
    }

    fun sendLandmarks(landmarks: List<Pair<Float, Float>>) {
        Log.d("PoseLandmarkMessenger", "Sending landmarks: $landmarks")
        mainHandler.post {
            try{
                eventSink?.success(landmarks.map { listOf(it.first, it.second) })
            } catch (e: Exception) {
                Log.e("PoseLandmarkMessenger", "Error sending landmarks", e)
            }
        }
    }
}
