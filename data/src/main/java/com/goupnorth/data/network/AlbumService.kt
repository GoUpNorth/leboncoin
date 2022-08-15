package com.goupnorth.data.network

import com.goupnorth.data.network.models.AlbumDto
import retrofit2.http.GET

/**
 * Retrofit service
 */
interface AlbumService {

    @GET("/img/shared/technical-test.json")
    suspend fun getAlbums(): List<AlbumDto>
}