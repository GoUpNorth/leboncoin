package com.goupnorth.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goupnorth.data.db.daos.AlbumDao
import com.goupnorth.domain.models.Album

@Database(entities = [Album::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun albumDao(): AlbumDao
}
