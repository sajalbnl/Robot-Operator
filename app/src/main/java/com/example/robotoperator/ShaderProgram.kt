package com.example.robotoperator


import android.content.Context
import android.opengl.GLES20
import android.util.Log

class ShaderProgram(private val context: Context) {

    private val vertexShaderCode = """
        precision mediump float;

        attribute vec3 a_Position;
        attribute vec3 a_Color;
        uniform mat4 u_MVPMatrix;
        varying vec3 v_Color;

        void main() {
            gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
            v_Color = a_Color;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec3 v_Color;

        void main() {
            gl_FragColor = vec4(v_Color, 1.0);
        }
    """.trimIndent()

    private var program: Int

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = linkProgram(vertexShader, fragmentShader)
    }

    fun use() {
        GLES20.glUseProgram(program)
    }

    fun getMVPMatrixHandle(): Int {
        return GLES20.glGetUniformLocation(program, "u_MVPMatrix")
    }

    fun getPositionHandle(): Int {
        return GLES20.glGetAttribLocation(program, "a_Position")
    }

    fun getColorHandle(): Int {
        return GLES20.glGetAttribLocation(program, "a_Color")
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)

        if (status[0] == 0) {
            val errorLog = GLES20.glGetShaderInfoLog(shader)
            Log.e("ShaderProgram", "Shader compilation failed:\n$errorLog\nShader Code:\n$shaderCode")
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed: $errorLog")
        }
        return shader
    }




    private fun linkProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val status = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)

        if (status[0] == 0) {
            Log.e("ShaderProgram", "Program linking failed: ${GLES20.glGetProgramInfoLog(program)}")
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Program linking failed.")
        }
        return program
    }
}
