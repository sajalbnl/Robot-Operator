package com.example.robotoperator

import android.content.Context
import android.util.Log

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class PLYModel(val vertices: FloatArray, val indices: ShortArray)

object PLYLoader {
    fun loadPLY(context: Context, fileName: String): PLYModel {
        val bytes = context.assets.open("models/$fileName").readBytes()
        val fileText = String(bytes, Charsets.US_ASCII)
        val headerEndString = "end_header"
        val headerEndIndex = fileText.indexOf(headerEndString)
        if (headerEndIndex == -1) throw IllegalArgumentException("Invalid PLY file: No end_header found")

        val headerEndOffset = headerEndIndex + headerEndString.length + 1
        val headerLines = fileText.substring(0, headerEndOffset).split("\n")

        var vertexCount = 0
        var faceCount = 0
        for (line in headerLines) {
            when {
                line.startsWith("element vertex") -> vertexCount = line.split(" ")[2].toInt()
                line.startsWith("element face") -> faceCount = line.split(" ")[2].toInt()
            }
        }
        Log.d("PLYLoader", "Vertex Count: $vertexCount, Face Count: $faceCount")

        val buffer = ByteBuffer.wrap(bytes, headerEndOffset, bytes.size - headerEndOffset)
        buffer.order(ByteOrder.BIG_ENDIAN)
        if (buffer.remaining() < vertexCount * 12) {  // 3 floats per vertex = 12 bytes
            throw IllegalArgumentException("PLY file is corrupted: Not enough data for vertices")
        }


        val vertices = FloatArray(vertexCount * 3)
        for (i in 0 until vertexCount) {
            val xBytes = ByteArray(4)
            val yBytes = ByteArray(4)
            val zBytes = ByteArray(4)

            buffer.get(xBytes)
            buffer.get(yBytes)
            buffer.get(zBytes)

            vertices[i * 3] = ByteBuffer.wrap(xBytes).order(ByteOrder.LITTLE_ENDIAN).float
            vertices[i * 3 + 1] = ByteBuffer.wrap(yBytes).order(ByteOrder.LITTLE_ENDIAN).float
            vertices[i * 3 + 2] = ByteBuffer.wrap(zBytes).order(ByteOrder.LITTLE_ENDIAN).float
        }

        normalizeVertices(vertices)
        Log.d("PLYLoader", "First 5 vertices: ${vertices.take(15).joinToString()}")

        // If no faces exist, return only vertices (Point Cloud Mode)
        if (faceCount == 0) {
            Log.w("PLYLoader", "No faces found! The model is a point cloud, not a mesh.")
            return PLYModel(vertices, ShortArray(0)) // No indices
        }

        val indicesList = mutableListOf<Short>()
        for (i in 0 until faceCount) {
            val vertexPerFace = buffer.get().toInt() and 0xFF
            if (vertexPerFace < 3) continue
            val faceVertices = ShortArray(vertexPerFace) { (buffer.int and 0xFFFF).toShort() }
            for (j in 1 until vertexPerFace - 1) {
                indicesList.add(faceVertices[0])
                indicesList.add(faceVertices[j])
                indicesList.add(faceVertices[j + 1])
            }
        }

        val indices = indicesList.toShortArray()
        Log.d("PLYLoader", "First vertex: (${vertices[0]}, ${vertices[1]}, ${vertices[2]})")
        Log.d("PLYLoader", "First 5 vertices: ${vertices.take(15).joinToString()}")

        return PLYModel(vertices, indices)
    }

    private fun normalizeVertices(vertices: FloatArray) {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        var maxZ = -Float.MAX_VALUE

        for (i in vertices.indices step 3) {
            val x = vertices[i]
            val y = vertices[i + 1]
            val z = vertices[i + 2]
            minX = minOf(minX, x)
            minY = minOf(minY, y)
            minZ = minOf(minZ, z)
            maxX = maxOf(maxX, x)
            maxY = maxOf(maxY, y)
            maxZ = maxOf(maxZ, z)
        }

        val centerX = (minX + maxX) / 2f
        val centerY = (minY + maxY) / 2f
        val centerZ = (minZ + maxZ) / 2f
        val maxDimension = maxOf(maxX - minX, maxY - minY, maxZ - minZ)
        val scale = 2.0f / maxDimension

        for (i in vertices.indices step 3) {
            vertices[i] = (vertices[i] - centerX) * scale
            vertices[i + 1] = (vertices[i + 1] - centerY) * scale
            vertices[i + 2] = (vertices[i + 2] - centerZ) * scale
        }
    }
}
