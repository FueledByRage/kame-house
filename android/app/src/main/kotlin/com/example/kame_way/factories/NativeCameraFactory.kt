package com.example.kame_way.factories

import android.content.Context
import com.example.kame_way.view.NativeCameraView
import io.flutter.embedding.engine.plugins.FlutterPlugin

import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import androidx.lifecycle.LifecycleOwner
import com.example.kame_way.MainActivity
import io.flutter.plugin.common.MethodChannel
import com.example.kame_way.shared.enums.Channels
import com.example.kame_way.shared.enums.ChannelMethods
import io.flutter.plugin.common.BinaryMessenger

class NativeCameraFactory(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val messenger: BinaryMessenger
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val view = NativeCameraView(context, lifecycleOwner)

        createToggleCameraChannel(context, view)

        return view
    }

    private fun createToggleCameraChannel(context: Context, view : NativeCameraView) {
        val toggleCameraChannell = MethodChannel(messenger, Channels.CAMERA.toString())

        toggleCameraChannell.setMethodCallHandler { call, result -> 
            when (call.method) {
                ChannelMethods.TOGGLE_CAMERA.toString() -> {
                    view.toggleCamera()
                    result.success(null)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
}
