package com.example.robotoperator

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import android.opengl.Matrix
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
//
//class ModelRenderer(context: Context, private val glSurfaceView: ModelGLSurfaceView) : GLSurfaceView.Renderer {
//    private var model: PlyModel? = null
//    private var vertexBuffer: FloatBuffer? = null
//    private var indexBuffer: IntBuffer? = null
//    private val viewMatrix = FloatArray(16)
//    private val projectionMatrix = FloatArray(16)
//    private var program: Int = 0
//
//    init {
//        // Load the model from the .ply file
//        val inputStream = context.assets.open("models/room.ply")
//        model = parsePlyFile(inputStream)
//        model?.let {
//            vertexBuffer = ByteBuffer.allocateDirect(it.vertices.size * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(it.vertices)
//            vertexBuffer?.position(0)
//
//            indexBuffer = ByteBuffer.allocateDirect(it.indices.size * 4)
//                .order(ByteOrder.nativeOrder())
//                .asIntBuffer()
//                .put(it.indices)
//            indexBuffer?.position(0)
//        }
//    }
//
//    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
//        program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
//    }
//
//    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        GLES20.glViewport(0, 0, width, height)
//        Matrix.perspectiveM(projectionMatrix, 0, 45f, width.toFloat() / height, 0.1f, 100f)
//        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
//    }
//
//    override fun onDrawFrame(gl: GL10?) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//        GLES20.glUseProgram(program)
//
//        // Get the updated model matrix from the GLSurfaceView
//        val modelMatrix = glSurfaceView.getModelMatrix()
//
//        // Calculate the MVP matrix
//        val mvpMatrix = FloatArray(16)
//        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
//        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
//
//        // Draw the model
//        vertexBuffer?.let { vb ->
//            indexBuffer?.let { ib ->
//                val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
//                GLES20.glEnableVertexAttribArray(positionHandle)
//                GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vb)
//
//                val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
//                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
//
//                GLES20.glDrawElements(GLES20.GL_TRIANGLES, ib.remaining(), GLES20.GL_UNSIGNED_INT, ib)
//                GLES20.glDisableVertexAttribArray(positionHandle)
//            }
//        }
//    }
//
//    private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
//        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
//        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
//        val program = GLES20.glCreateProgram()
//        GLES20.glAttachShader(program, vertexShader)
//        GLES20.glAttachShader(program, fragmentShader)
//        GLES20.glLinkProgram(program)
//        return program
//    }
//
//    private fun loadShader(type: Int, shaderCode: String): Int {
//        val shader = GLES20.glCreateShader(type)
//        GLES20.glShaderSource(shader, shaderCode)
//        GLES20.glCompileShader(shader)
//        return shader
//    }
//
//    companion object {
//        const val VERTEX_SHADER = """
//            attribute vec4 vPosition;
//            uniform mat4 uMVPMatrix;
//            void main() {
//                gl_Position = uMVPMatrix * vPosition;
//            }
//        """
//
//        const val FRAGMENT_SHADER = """
//            precision mediump float;
//            void main() {
//                gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
//            }
//        """
//    }
//}