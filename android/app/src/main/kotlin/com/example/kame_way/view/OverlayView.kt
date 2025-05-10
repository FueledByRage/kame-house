package com.example.kame_way.view

import android.content.Context
import android.graphics.*
import android.view.View

class PoseOverlayView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 8f
        style = Paint.Style.FILL
    }

    private var points: List<Pair<Float, Float>> = emptyList()

    fun setLandmarks(landmarks: List<Pair<Float, Float>>) {
        this.points = landmarks
        invalidate() // força redesenho
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((x, y) in points) {
            canvas.drawCircle(x * width, y * height, 10f, paint)
        }
    }
}
