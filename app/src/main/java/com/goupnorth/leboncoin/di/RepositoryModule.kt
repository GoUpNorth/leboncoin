package com.goupnorth.leboncoin.di

import com.goupnorth.data.db.daos.AlbumDao
import com.goupnorth.data.mappers.AlbumMapper
import com.goupnorth.data.network.AlbumService
import com.goupnorth.data.repositories.AlbumRepositoryImpl
import com.goupnorth.domain.repositories.AlbumRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providesAlbumRepository(
        albumService: AlbumService,
        albumDao: AlbumDao,
        albumMapper: AlbumMapper
    ): AlbumRepository {
        return AlbumRepositoryImpl(albumService, albumDao, albumMapper)
    }
}