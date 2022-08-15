package com.goupnorth.leboncoin.di

import android.app.Application
import androidx.room.Room
import com.goupnorth.data.db.AppDatabase
import com.goupnorth.data.db.daos.AlbumDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesAlbumsDao(appDatabase: AppDatabase): AlbumDao = appDatabase.albumDao()
}