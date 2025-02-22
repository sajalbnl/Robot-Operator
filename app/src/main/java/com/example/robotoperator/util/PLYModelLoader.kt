package com.example.robotoperator.util

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PLYModelLoader(private val context: Context) {

    fun loadPLYModel(filename: String): FloatArray? {
        return try {
            val inputStream: InputStream = context.assets.open("models/temp.ply") // full path
            parsePLY(inputStream,filename)
        } catch (e: Exception) {
            Log.e("PLYModelLoader", "Error loading PLY file: $filename", e)
            null
        }
    }


    private fun parsePLY(inputStream: InputStream, filename: String): FloatArray? {
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))

            var vertexCount = 0
            var readingHeader = true
            val vertexList = mutableListOf<Float>()
            var binaryFormat = false
            var littleEndian = true
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                if (readingHeader) {
                    if (line?.startsWith("element vertex") == true) {
                        vertexCount = line.split(" ")[2].toInt()
                        Log.d("PLYModelLoader", "Vertex count: $vertexCount")
                    } else if (line?.startsWith("format binary_little_endian") == true) {
                        binaryFormat = true
                        littleEndian = true
                        Log.d("PLYModelLoader", "Binary Little Endian PLY")
                    } else if (line?.startsWith("format binary_big_endian") == true) {
                        binaryFormat = true
                        littleEndian = false
                        Log.d("PLYModelLoader", "Binary Big Endian PLY")
                    } else if (line == "end_header") {
                        readingHeader = false
                        Log.d("PLYModelLoader", "End of header")
                        if (binaryFormat) {
                            break // Stop reading lines if binary
                        }
                    }
                }
            }


            if (binaryFormat) {
                inputStream.close()
                val dataInputStream = DataInputStream(context.assets.open("models/$filename"))
                val availableBytes = dataInputStream.available();
                val byteBuffer = ByteBuffer.allocate(availableBytes)
                byteBuffer.order(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)

                var headerSize = 0
                val reader2 = BufferedReader(InputStreamReader(context.assets.open("models/$filename")))
                while (true) {
                    val line2 = reader2.readLine() ?: break
                    headerSize += line2.length + 1
                    if (line2 == "end_header") {
                        break
                    }
                }
                Log.d("PLYModelLoader", "Header Size: $headerSize")
                dataInputStream.skip(headerSize.toLong())
                Log.d("PLYModelLoader", "Available Bytes after Skip: ${dataInputStream.available()}")

                dataInputStream.read(byteBuffer.array())
                Log.d("PLYModelLoader", "Bytes Read: ${byteBuffer.array().size}")

                //Parse the byte buffer, and extract the vertex data.
                val floatBuffer = byteBuffer.asFloatBuffer();
                val floatArray = FloatArray(vertexCount * 3);
                floatBuffer.get(floatArray);

                Log.d("PLYModelLoader", "Loaded ${floatArray.size / 3} vertices")
                return floatArray;
            }
            else {
                // Your existing ASCII parsing logic
                inputStream.bufferedReader().lines().skip(1).forEach { line ->
                    val parts = line.split(" ")
                    if (parts.size >= 3) {
                        vertexList.add(parts[0].toFloat())
                        vertexList.add(parts[1].toFloat())
                        vertexList.add(parts[2].toFloat())
                    }
                }
                if (vertexList.size != vertexCount * 3) {
                    Log.e("PLYModelLoader", "Vertex count mismatch: Expected ${vertexCount * 3}, got ${vertexList.size}")
                    return null
                }
                return vertexList.toFloatArray()
            }

        } catch (e: Exception) {
            Log.e("PLYModelLoader", "Error parsing PLY file", e)
            e.printStackTrace()
            return null
        }
    }
}