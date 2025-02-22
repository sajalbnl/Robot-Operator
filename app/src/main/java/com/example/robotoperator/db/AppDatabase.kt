package com.example.robotoperator.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.robotoperator.data.model.AnnotationEntity

@Database(entities = [AnnotationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun annotationDao(): AnnotationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "annotations.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}