package com.example.robotoperator

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.robotoperator.util.createProgramFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
            gl_FragColor = vec4(0.0, 0.5, 1.0, 1.0);
        }
    """.trimIndent()

    private var program = 0
    private var positionHandle = 0
    private var mvpMatrixHandle = 0
    private lateinit var vertexBuffer: FloatBuffer
    private var  vertexCount = 0

    // Matrices for transformations
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var angleX = 0f
    private var angleY = 0f
    private var scale = 100.0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1.0f)

        program = createProgram(vertexShaderCode, fragmentShaderCode)

        // Load and parse PLY file
        loadPLY(context)

        GLES20.glEnableVertexAttribArray(positionHandle)
    }
    private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        val program = GLES20.glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Error creating OpenGL program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val errorMsg = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Error linking program: $errorMsg")
        }

        return program
    }
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            val errorMsg = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Error compiling shader: $errorMsg")
        }

        return shader
    }



    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
    }

    private fun loadPLY(context: Context) {
        val inputStream = context.assets.open("models/temp.ply")
        val buffer = inputStream.readBytes()
        inputStream.close()

        // Convert header to a string for processing
        val header = buffer.decodeToString()
        val headerEndIndex = header.indexOf("\nend_header\n") + 11 // Move past "end_header\n"

        if (headerEndIndex == -1) {
            throw RuntimeException("Invalid PLY file: No end_header found")
        }


        // Extract number of vertices using regex
        val vertexCountRegex = Regex("element vertex (\\d+)")
        val match = vertexCountRegex.find(header)

        if (match == null) {
            throw RuntimeException("Invalid PLY file: Vertex count not found")
        }

        val numVertices = match.groupValues[1].toIntOrNull()
            ?: throw RuntimeException("Invalid vertex count format")

        vertexCount = numVertices

        // Debug: Print vertex count for verification
        println("Vertex Count: $numVertices")

        // Calculate the correct start of vertex data
        val vertexDataStart = headerEndIndex
        val vertexDataSize = numVertices * 12 // 3 floats (x, y, z) * 4 bytes each

        if (vertexDataStart + vertexDataSize > buffer.size) {
            throw RuntimeException("Invalid PLY file: Insufficient vertex data")
        }

        // Extract vertex data
        val vertexData = buffer.sliceArray(vertexDataStart until (vertexDataStart + vertexDataSize))

        // Convert to float buffer
        val floatBuffer = ByteBuffer.wrap(vertexData).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
        val vertices = FloatArray(numVertices * 3)
        floatBuffer.get(vertices)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices).position(0)
    }



    fun rotate(dx: Float, dy: Float) {
        angleX += dx * 0.5f
        angleY += dy * 0.5f
        Matrix.setRotateM(modelMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, angleY, 1f, 0f, 0f)
    }

    fun zoom(factor: Float) {
        scale *= factor
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale)
    }
}
