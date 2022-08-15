package com.goupnorth.leboncoin.ui.album

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.goupnorth.data.db.daos.AlbumDao
import com.goupnorth.data.mappers.AlbumMapper
import com.goupnorth.data.network.AlbumService
import com.goupnorth.data.repositories.AlbumRepositoryImpl
import com.goupnorth.domain.interactors.AllAlbumsPaginatedInteractor
import com.goupnorth.domain.interactors.RefreshAlbumsInteractor
import com.goupnorth.domain.repositories.AlbumRepository
import com.goupnorth.leboncoin.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException


@RunWith(RobolectricTestRunner::class)
class AlbumsViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var albumService: AlbumService
    private lateinit var albumDao: AlbumDao
    private lateinit var repository: AlbumRepository
    private lateinit var refreshInteractor: RefreshAlbumsInteractor
    private lateinit var allAlbumsInteractor: AllAlbumsPaginatedInteractor
    private lateinit var viewModel: AlbumsViewModel

    @Before
    fun setup() {
        albumService = mockk(relaxed = true)
        albumDao = mockk(relaxed = true)
        repository =
            AlbumRepositoryImpl(albumService, albumDao, AlbumMapper(), Dispatchers.Unconfined)
        refreshInteractor = RefreshAlbumsInteractor(repository)
        allAlbumsInteractor = AllAlbumsPaginatedInteractor(repository)
        viewModel = AlbumsViewModel(refreshInteractor, allAlbumsInteractor, SavedStateHandle())
    }

    @Test
    fun `when loading albums, isLoading updated`() = runTest {
        viewModel.state.test {
            assertEquals(false, awaitItem().isLoading)
            viewModel.loadAlbums()
            assertEquals(true, awaitItem().isLoading)
            assertEquals(false, awaitItem().isLoading)
        }
    }

    @Test
    fun `when loading fails, isError updated`() = runTest {
        coEvery { albumService.getAlbums() } throws IOException()
        viewModel.loadAlbums()
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.isError)
    }

    @Test
    fun `when error message shown, isError is reset`() {
        viewModel.errorMessageShown()
        assertEquals(false, viewModel.state.value.isError)
    }
}
