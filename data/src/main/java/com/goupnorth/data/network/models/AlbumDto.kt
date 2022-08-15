package com.goupnorth.data.network.models

import com.squareup.moshi.JsonClass

// Let moshi generate an adapter at runtime to avoid using reflection at deserialization
@JsonClass(generateAdapter = true)
data class AlbumDto(
    val albumId: Long,
    val id: Long,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)
