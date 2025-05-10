package com.example.kame_way.listeners

import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.EventChannel.StreamHandler

class PoseStreamListener : EventChannel.StreamHandler {
  private var eventSink: EventChannel.EventSink? = null

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
      eventSink = events
  }

  override fun onCancel(arguments: Any?) {
      eventSink = null
  }

  fun send(landmarks: List<Pair<Float, Float>>) {
      val data = landmarks.map { listOf(it.first, it.second) }
      eventSink?.success(data)
  }
}
