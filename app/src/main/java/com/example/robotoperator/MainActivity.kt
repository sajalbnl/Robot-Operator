package com.example.robotoperator


import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.ScaleGestureDetector
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.example.robotoperator.ui.theme.RobotOperatorTheme
import com.example.robotoperator.util.PlyRenderer
import kotlin.math.sqrt


class MainActivity : ComponentActivity() {
    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: PlyRenderer
    private lateinit var scaleDetector: ScaleGestureDetector
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val model = PLYLoader.loadPLY(this, "room.ply")
        renderer = PlyRenderer(model)

        glView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                renderer.zoom(if (scaleFactor > 1) -0.5f else 0.5f)
                return true
            }
        })
        setContent {
            RobotOperatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color("#333333".toColorInt())) {
                    Room3DView()
                }
            }
        }
    }
}
@Composable
fun Room3DView() {
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            val view = PlyGLSurfaceView(context)
            view
        }
    )


}

//
//@Composable
//fun OpenGLView(fileName: String) {
//    val context = LocalContext.current
//    val glSurfaceView = remember {
//        MyGLSurfaceView(context).apply {
//            setEGLContextClientVersion(2)
//            val renderer = MyGLRenderer(context, fileName)
//            setMyRenderer(renderer)
//        }
//    }
//
//    val renderer = remember { glSurfaceView.getMyRenderer() }
//
//    var previousX = 0f
//    var previousY = 0f
//    var initialDistance = 0f
//    var currentDistance = 0f
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(renderer) {
//                if (renderer != null) {
//                    detectDragGestures(
//                        onDragStart = { offset ->
//                            previousX = offset.x
//                            previousY = offset.y
//                        },
//                        onDrag = { change, dragAmount ->
//                            val x = change.position.x
//                            val y = change.position.y
//                            val dx = x - previousX
//                            val dy = y - previousY
//                            renderer.angleY += dx * 0.5f
//                            renderer.angleX += dy * 0.5f
//                            previousX = x
//                            previousY = y
//                        },
//                        onDragEnd = {}
//                    )
//                }
//            }
//            .pointerInput(renderer) {
//                if(renderer != null){
//                    detectDragGestures(
//                        onDragStart = { offset ->
//                            initialDistance = 0f
//                            currentDistance = 0f
//                        },
//                        onDrag = { change, dragAmount ->
//                            val x = change.position.x
//                            val y = change.position.y
//                            if (initialDistance == 0f) {
//                                initialDistance = sqrt(x * x + y * y)
//                            }
//                            currentDistance = sqrt(x * x + y * y)
//                            val zoomChange = (currentDistance - initialDistance) / 100
//                            renderer.zoom -= zoomChange
//                        },
//                        onDragEnd = {}
//                    )
//                }
//            }
//    ) {
//        AndroidView(factory = { glSurfaceView })
//    }
//}
//@Composable
//fun AndroidView(factory: () -> GLSurfaceView) {
//    androidx.compose.ui.viewinterop.AndroidView(factory = { factory() })
//}