package com.example.robotoperator.ui.theme.views


import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.robotoperator.util.PLYModelLoader
import com.example.robotoperator.util.createProgramFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RoomRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var vertices: FloatArray? = null
    private var plyLoader: PLYModelLoader = PLYModelLoader(context)

    private var mvpMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var modelMatrix = FloatArray(16)

    private var shaderProgram: Int = 0
    private var aPositionLocation: Int = 0
    private var uMVPMatrixLocation: Int = 0
    private var uColorLocation: Int = 0

    private var vertexBuffer: FloatBuffer? = null

    private var cameraX = 0f
    private var cameraY = 0f
    private var cameraZoom = 1f

    private var vertexBufferId = IntArray(1);


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        shaderProgram = createProgramFromAssets(context, "vertex_shader.glsl", "fragment_shader.glsl")
        GLES20.glUseProgram(shaderProgram)

        aPositionLocation = GLES20.glGetAttribLocation(shaderProgram, "a_Position")
        uMVPMatrixLocation = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix")
        uColorLocation = GLES20.glGetUniformLocation(shaderProgram, "u_Color")

        vertices = plyLoader.loadPLYModel("temp.ply")
        Log.d("PLYModelLoader", "Loaded ${vertices?.size ?: 0} vertices")

        vertices?.let {
            val byteBuffer = ByteBuffer.allocateDirect(it.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            vertexBuffer?.put(it)
            vertexBuffer?.position(0)
        }
        GLES20.glGenBuffers(1, vertexBufferId, 0); //Generate the buffer ID.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId[0]); //Bind the buffer.
        vertexBuffer?.let {
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, it.capacity()*4, it, GLES20.GL_STATIC_DRAW); //Load the data.
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0); //Unbind the buffer.
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, cameraX, cameraY, cameraZoom * 5f, cameraX, cameraY, 0f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)

        vertexBuffer?.let {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId[0]); //Bind the buffer.
            GLES20.glEnableVertexAttribArray(aPositionLocation)
            GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false, 0, 0) //offset is 0 now.
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices?.size?.div(3) ?: 0)
            GLES20.glDisableVertexAttribArray(aPositionLocation)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0); //Unbind the buffer.
        }
    }
//
//  private var vertices: FloatArray? = null
//    private var plyLoader: PLYModelLoader = PLYModelLoader(context)
//
//    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f) // Background color
//
//
//        // Load 3D models asynchronously
//        vertices = plyLoader.loadPLYModel("room.ply")
//        Log.d("PLYModelLoader", "Loaded ${vertices?.size ?: 0} vertices")
//    }
//
//    override fun onDrawFrame(gl: GL10?) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//
//        vertices?.let { drawPLYModel(it) }
//    }
//
//
//    private var cameraX = 0f
//    private var cameraY = 0f
//    private var cameraZoom = 1f
//
    fun moveCamera(dx: Float, dy: Float) {
        cameraX += dx * 0.01f
        cameraY += dy * 0.01f
    }
//
    fun zoomCamera(scaleFactor: Float) {
        cameraZoom *= scaleFactor
    }

    fun addAnnotation(screenX: Float, screenY: Float) {
      //  val worldPosition = convertScreenToWorld(screenX, screenY) ?: return

        // Store the annotation in a list or database
      //  annotations.add(worldPosition)

        // Log or display feedback
        Log.d("RoomRenderer", "Annotation added at: ")
    }

    fun handleTap(screenX: Float, screenY: Float) {
//        val tappedPosition = convertScreenToWorld(screenX, screenY) ?: return
//
//        // Check if any annotation exists at this location
//        for (annotation in annotations) {
//            if (isNearby(annotation, tappedPosition)) {
//                Log.d("RoomRenderer", "Annotation selected at: $annotation")
//                return
//            }
//        }

        Log.d("RoomRenderer", "No annotation found at tap location.")
    }


    fun flingCamera(velocityX: Float, velocityY: Float) {
        val speedFactor = 0.05f  // Adjust speed
//
//        // Convert velocity into camera movement
//        cameraPosition.x += velocityX * speedFactor
//        cameraPosition.y += velocityY * speedFactor

        Log.d("RoomRenderer", "Fling camera to:")
    }


    private fun drawPLYModel(vertices: FloatArray) {
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        GLES20.glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 3)
        GLES20.glDisableVertexAttribArray(0)
    }


//    private val projectionMatrix = FloatArray(16)
//
//    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int){
//        // Set the OpenGL viewport to the new size
//        GLES20.glViewport(0, 0, width, height)
//
//        // Set up the projection matrix
//        val aspectRatio = width.toFloat() / height.toFloat()
//
//        // Create a perspective projection matrix
//        Matrix.frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 3f, 7f)
//    }

}
