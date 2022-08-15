package com.goupnorth.domain.interactors

import com.goupnorth.domain.repositories.AlbumRepository
import javax.inject.Inject

class RefreshAlbumsInteractor @Inject constructor(private val repository: AlbumRepository) {

    suspend fun execute() {
        return repository.refreshAlbums()
    }
}