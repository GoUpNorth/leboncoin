package com.goupnorth.data.mappers

import com.goupnorth.data.network.models.AlbumDto
import com.goupnorth.domain.models.Album
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumMapperTest {

    @Test
    fun `test dto to domain mapping`() {
        val mapper = AlbumMapper()
        val albums = List(2) {
            Album(
                id = it.toLong(),
                title = "title$it",
                url = "url$it",
                thumbnailUrl = "thumbnailUrl$it"
            )
        }
        val dtos = List(2) {
            AlbumDto(
                id = it.toLong(),
                albumId = it.toLong(),
                title = "title$it",
                url = "url$it",
                thumbnailUrl = "thumbnailUrl$it"
            )
        }

        assertEquals(albums, dtos.map { dto -> mapper.toAlbum(dto) })
    }
}