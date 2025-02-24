package com.example.robotoperator.ui.theme.views

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.sqrt

//class GLRoomView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs), GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
//
//    private val renderer: RoomRenderer = RoomRenderer(context)
//    private val gestureDetector = GestureDetector(context, this)
//    private val scaleDetector = ScaleGestureDetector(context, this)
//
//    init {
//        setEGLContextClientVersion(2)
//        setRenderer(renderer)
//        renderMode = RENDERMODE_CONTINUOUSLY
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        gestureDetector.onTouchEvent(event)
//        scaleDetector.onTouchEvent(event)
//        return true
//    }
//
//    /** Handles zoom gestures */
//    override fun onScale(detector: ScaleGestureDetector): Boolean {
//        renderer.zoomCamera(detector.scaleFactor)
//        return true
//    }
//
//    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
//        return true // Indicate we are handling zoom gestures
//    }
//
//    override fun onScaleEnd(detector: ScaleGestureDetector) {
//        // No action needed
//    }
//
//    /** Detects when a touch is pressed */
//    override fun onDown(event: MotionEvent): Boolean {
//        return true // Required for other gestures to work
//    }
//
//    override fun onShowPress(event: MotionEvent) {
//        // Can be used for UI feedback when the user taps on the screen
//    }
//
//    /** Handles single taps (e.g., selecting an object) */
//    override fun onSingleTapUp(event: MotionEvent): Boolean {
//        renderer.handleTap(event.x, event.y) // Implement object selection in RoomRenderer
//        return true
//    }
//
//    /** Handles panning (camera movement) */
//    override fun onScroll(
//        e1: MotionEvent?, e2: MotionEvent,
//        distanceX: Float, distanceY: Float
//    ): Boolean {
//        renderer.moveCamera(-distanceX, -distanceY) // Move the camera
//        return true
//    }
//
//    /** Detects long-press gestures (e.g., adding annotations) */
//    override fun onLongPress(event: MotionEvent) {
//        renderer.addAnnotation(event.x, event.y) // Implement annotation placement in RoomRenderer
//    }
//
//    /** Handles swipe (fling) gestures */
//    override fun onFling(
//        e1: MotionEvent?, e2: MotionEvent,
//        velocityX: Float, velocityY: Float
//    ): Boolean {
//        renderer.flingCamera(velocityX, velocityY) // Implement smooth camera movement
//        return true
//    }
//}
//

//@SuppressLint("ClickableViewAccessibility")
//class GLRoomView(context: Context) : GLSurfaceView(context) {
//    private val renderer: PlyRenderer
//    private val gestureDetector: GestureDetector
//
//    init {
//        setEGLContextClientVersion(3)
//        renderer = PlyRenderer(context)
//        setRenderer(renderer)
//        renderMode = RENDERMODE_CONTINUOUSLY
//
//        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
//            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
//                renderer.moveCamera(-distanceX / width, distanceY / height)
//                return true
//            }
//            override fun onDoubleTap(e: MotionEvent): Boolean {
//                renderer.resetCamera()
//                return true
//            }
//        })
//
//        setOnTouchListener { _, event ->
//            if (event.pointerCount == 2) { // Pinch Zoom
//                val dx = event.getX(0) - event.getX(1)
//                val dy = event.getY(0) - event.getY(1)
//                val newDistance = sqrt(dx * dx + dy * dy)
//                renderer.setZoom(newDistance)
//            } else {
//                gestureDetector.onTouchEvent(event)
//            }
//            true
//        }
//    }
//}
