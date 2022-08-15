package com.goupnorth.data.mappers

import com.goupnorth.domain.models.Album
import com.goupnorth.data.network.models.AlbumDto
import javax.inject.Inject

class AlbumMapper @Inject constructor() {

    fun toAlbum(dto: AlbumDto) = Album(
        id = dto.id,
        title = dto.title,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl
    )
}