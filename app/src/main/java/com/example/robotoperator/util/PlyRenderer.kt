package com.example.robotoperator.util

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.robotoperator.PLYLoader
import com.example.robotoperator.PLYModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.max
import kotlin.math.min

class PlyRenderer(private val model: PLYModel) : GLSurfaceView.Renderer {
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private var program = 0

    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private var zoom = -3.0f  // Default zoom
    private val minZoom = -10f
    private val maxZoom = -1.5f  // Prevent zooming too close

    init {
        val bb = ByteBuffer.allocateDirect(model.vertices.size * 4).order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(model.vertices).position(0)

        val ib = ByteBuffer.allocateDirect(model.indices.size * 2).order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(model.indices).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1.0f)  // Light grey background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        program = GLES20.glCreateProgram().also {
            val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).apply {
                GLES20.glShaderSource(this, vertexShaderCode)
                GLES20.glCompileShader(this)
            }
            val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).apply {
                GLES20.glShaderSource(this, fragmentShaderCode)
                GLES20.glCompileShader(this)
            }
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Ensure zoom is within limits
        zoom = min(maxZoom, max(minZoom, zoom))

        // Update View Matrix
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, zoom, 0f, 0f, 0f, 0f, 1f, 0f)

        // Combine Projection and View Matrix
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(program)
        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawElements(GLES20.GL_POINTS, model.indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 20f)
    }

    // Public function to zoom in/out
    fun zoom(delta: Float) {
        zoom += delta
        zoom = min(maxZoom, max(minZoom, zoom))  // Clamp zoom to prevent extreme values
    }

    private val vertexShaderCode = """
        attribute vec4 a_Position;
        void main() {
            gl_Position = a_Position;
            gl_PointSize = 100.0; // Adjust size for visibility
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 u_Color;
        void main() {
            gl_FragColor = u_Color;
        }
    """.trimIndent()
}
