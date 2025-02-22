package com.example.robotoperator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "annotations")
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Spray", "Sand", "Obstacle"
    val x: Float,
    val y: Float,
    val z: Float
)