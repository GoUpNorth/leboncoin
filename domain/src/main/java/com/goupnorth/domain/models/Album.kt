package com.goupnorth.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Album.TABLE)
data class Album(
    @PrimaryKey val id: Long,
    val title: String,
    val url: String,
    @ColumnInfo(name = THUMBNAIL_URL) val thumbnailUrl: String
) {
    companion object {
        const val TABLE = "albums"

        private const val THUMBNAIL_URL = "THUMBNAIL_URL"
    }
}
