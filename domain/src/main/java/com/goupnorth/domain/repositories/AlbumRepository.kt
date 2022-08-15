package com.goupnorth.domain.repositories

import androidx.paging.PagingSource
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType

interface AlbumRepository {

    suspend fun refreshAlbums()

    fun observeAlbums(sortType: SortType, sortOrder: SortOrder): PagingSource<Int, Album>
}