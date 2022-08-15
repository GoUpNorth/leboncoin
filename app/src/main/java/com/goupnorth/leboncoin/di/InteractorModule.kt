package com.goupnorth.leboncoin.di

import com.goupnorth.domain.interactors.AllAlbumsPaginatedInteractor
import com.goupnorth.domain.interactors.RefreshAlbumsInteractor
import com.goupnorth.domain.repositories.AlbumRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class InteractorModule {

    @Provides
    fun providesRefreshAlbumsInteractor(albumRepository: AlbumRepository): RefreshAlbumsInteractor {
        return RefreshAlbumsInteractor(albumRepository)
    }

    @Provides
    fun providesAllAlbumsPaginatedInteractor(albumRepository: AlbumRepository): AllAlbumsPaginatedInteractor {
        return AllAlbumsPaginatedInteractor(albumRepository)
    }
}