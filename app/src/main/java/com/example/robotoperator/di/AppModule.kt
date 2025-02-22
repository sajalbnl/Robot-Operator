package com.example.robotoperator.di

import android.content.Context
import com.example.robotoperator.db.AnnotationDao
import com.example.robotoperator.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    @Provides
    fun provideAnnotationDao(database: AppDatabase): AnnotationDao =
        database.annotationDao()
}