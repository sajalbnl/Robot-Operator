package com.example.robotoperator.util

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun createProgramFromAssets(context: Context, vertexShaderFilename: String, fragmentShaderFilename: String): Int {
    val vertexShaderCode = loadShaderCodeFromAssets(context, vertexShaderFilename)
    val fragmentShaderCode = loadShaderCodeFromAssets(context, fragmentShaderFilename)

    val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

    val program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program, vertexShader)
    GLES20.glAttachShader(program, fragmentShader)
    GLES20.glLinkProgram(program)

    val linkStatus = IntArray(1)
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
    if (linkStatus[0] == 0) {
        val error = GLES20.glGetProgramInfoLog(program)
        GLES20.glDeleteProgram(program)
        throw RuntimeException("Could not link program: $error")
    }

    GLES20.glDeleteShader(vertexShader)
    GLES20.glDeleteShader(fragmentShader)

    return program
}

private fun loadShaderCodeFromAssets(context: Context, filename: String): String {
    try {
        val inputStream = context.assets.open(filename)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }

        return stringBuilder.toString()
    } catch (e: IOException) {
        throw RuntimeException("Could not open shader file: $filename", e)
    }
}

private fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    val compiled = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
    if (compiled[0] == 0) {
        val error = GLES20.glGetShaderInfoLog(shader)
        GLES20.glDeleteShader(shader)
        throw RuntimeException("Could not compile shader: $error")
    }

    return shader
}