package com.goupnorth.data.db.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.models.Album.Companion.TABLE
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType

@Dao
abstract class AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(albums: List<Album>)

    // It is important to execute both delete and insert in a Transaction
    // so that it doesn't clear the albums screen and then shows the new albums
    @Transaction
    open suspend fun replaceAll(albums: List<Album>) {
        deleteAll()
        insertAll(albums)
    }

    @Query("SELECT * FROM $TABLE")
    abstract suspend fun getAll(): List<Album>

    fun allAlbumsById(sortType: SortType, sortOrder: SortOrder): PagingSource<Int, Album> {
        return allAlbumsByIdInternal(sortType.ordinal, sortOrder == SortOrder.DESC)
    }

    @Query(
        """SELECT * FROM $TABLE ORDER BY 
		(CASE WHEN :sort = 1 and :isDesc = 0 THEN title
               WHEN :isDesc = 0 THEN id COLLATE LOCALIZED
          END) ASC,
         (CASE WHEN :sort = 1 and :isDesc = 1 THEN title
               WHEN :isDesc = 1 THEN id COLLATE LOCALIZED
          END) DESC"""
    )
    protected abstract fun allAlbumsByIdInternal(
        sort: Int,
        isDesc: Boolean
    ): PagingSource<Int, Album>

    @Query("DELETE FROM $TABLE")
    abstract suspend fun deleteAll()
}