package com.example.robotoperator

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class PlyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: PlyRenderer

    private var previousX = 0f
    private var previousY = 0f
    private var scaleFactor = 1.0f

    init {
        setEGLContextClientVersion(2)
        renderer = PlyRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.pointerCount) {
            1 -> { // Rotate
                if (event.action == MotionEvent.ACTION_MOVE) {
                    val dx = event.x - previousX
                    val dy = event.y - previousY
                    renderer.rotate(dx, dy)
                }
            }
            2 -> { // Zoom
                if (event.action == MotionEvent.ACTION_MOVE) {
                    val distance = Math.hypot(
                        (event.getX(0) - event.getX(1)).toDouble(),
                        (event.getY(0) - event.getY(1)).toDouble()
                    ).toFloat()

                    if (previousX != 0f) {
                        val scaleChange = distance / previousX
                        renderer.zoom(scaleChange)
                    }
                    previousX = distance
                }
            }
        }
        previousX = event.x
        previousY = event.y
        return true
    }
}
