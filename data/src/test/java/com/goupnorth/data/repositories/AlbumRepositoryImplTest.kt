package com.goupnorth.data.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.goupnorth.data.db.AppDatabase
import com.goupnorth.data.db.daos.AlbumDao
import com.goupnorth.data.mappers.AlbumMapper
import com.goupnorth.data.network.AlbumService
import com.goupnorth.data.network.models.AlbumDto
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.repositories.AlbumRepository
import com.goupnorth.data.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumRepositoryImplTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var albumService: AlbumService
    private lateinit var albumDao: AlbumDao
    private lateinit var database: AppDatabase
    private lateinit var repository: AlbumRepository
    private lateinit var mapper: AlbumMapper

    @Before
    fun setup() {
        albumService = mockk(relaxed = true)
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        albumDao = database.albumDao()
        repository =
            AlbumRepositoryImpl(albumService, albumDao, AlbumMapper(), Dispatchers.Unconfined)
        mapper = AlbumMapper()
    }

    @After
    fun clear() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun `albums saved in db after refresh`() = runTest {
        val dtos = listOf(
            AlbumDto(
                albumId = 1L,
                id = 1L,
                title = "title",
                url = "url",
                thumbnailUrl = "thumbnailUrl"
            )
        )
        coEvery { albumService.getAlbums() } returns dtos

        repository.refreshAlbums()
        advanceUntilIdle()
        assertEquals(listOf(mapper.toAlbum(dtos.first())), albumDao.getAll())
    }

    @Test
    fun `albums replaced in db after refresh`() = runTest {
        val album1 = Album(
            id = 1L,
            title = "title1",
            url = "url1",
            thumbnailUrl = "thumbnailUrl1"
        )
        albumDao.insertAll(listOf(album1))

        val album2 = AlbumDto(
            albumId = 2L,
            id = 2L,
            title = "title2",
            url = "url2",
            thumbnailUrl = "thumbnailUrl2"
        )
        coEvery { albumService.getAlbums() } returns listOf(album2)

        repository.refreshAlbums()
        advanceUntilIdle()
        assertEquals(listOf(mapper.toAlbum(album2)), albumDao.getAll())
    }
}