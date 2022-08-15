package com.goupnorth.data.repositories

import com.goupnorth.data.db.daos.AlbumDao
import com.goupnorth.data.mappers.AlbumMapper
import com.goupnorth.data.network.AlbumService
import com.goupnorth.domain.models.SortType
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.repositories.AlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumService: AlbumService,
    private val albumDao: AlbumDao,
    private val albumMapper: AlbumMapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AlbumRepository {

    override suspend fun refreshAlbums() = withContext(dispatcher) {
        val albums = albumService.getAlbums().map { albumMapper.toAlbum(it) }
        albumDao.replaceAll(albums)
    }

    override fun observeAlbums(sortType: SortType, sortOrder: SortOrder) =
        albumDao.allAlbumsById(sortType, sortOrder)
}