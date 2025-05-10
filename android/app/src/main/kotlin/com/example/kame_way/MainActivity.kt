package com.example.kame_way

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import androidx.annotation.NonNull
import io.flutter.plugin.common.EventChannel

import com.example.kame_way.factories.NativeCameraFactory
import com.example.kame_way.listeners.PoseStreamListener
import com.example.kame_way.shared.PoseLandmarkMessenger
import android.util.Log


class MainActivity : FlutterActivity(){

  private val CHANNEL = "com.example.kame_way/androidevent"
  private val poseStreamListener = PoseStreamListener()

  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)
    val messenger = flutterEngine.dartExecutor.binaryMessenger


    EventChannel(flutterEngine.dartExecutor.binaryMessenger, "pose_landmarks")
            .setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, sink: EventChannel.EventSink?) {
                    PoseLandmarkMessenger.setSink(sink)
                }

                override fun onCancel(arguments: Any?) {
                    PoseLandmarkMessenger.setSink(null)
                }
            })

    flutterEngine
    .platformViewsController
    .registry
    .registerViewFactory(
      "native-camera-view",
      NativeCameraFactory(this, this, messenger)
    )
  }

  fun sendLandmarksToFlutter(landmarks: List<Pair<Float, Float>>) {
    poseStreamListener.send(landmarks)
  }
}
