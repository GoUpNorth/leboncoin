package com.goupnorth.leboncoin.di

import com.goupnorth.data.network.AlbumService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    fun bindAlbumService(retrofit: Retrofit): AlbumService {
        return retrofit.create(AlbumService::class.java)
    }
}
