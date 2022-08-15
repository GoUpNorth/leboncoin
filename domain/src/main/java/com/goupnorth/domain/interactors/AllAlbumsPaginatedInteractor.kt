package com.goupnorth.domain.interactors

import androidx.paging.PagingSource
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType
import com.goupnorth.domain.repositories.AlbumRepository
import javax.inject.Inject

class AllAlbumsPaginatedInteractor @Inject constructor(private val repository: AlbumRepository) {

    fun execute(sortType: SortType, sortOrder: SortOrder): PagingSource<Int, Album> {
        return repository.observeAlbums(sortType, sortOrder)
    }
}