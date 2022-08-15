package com.goupnorth.leboncoin.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.goupnorth.domain.interactors.AllAlbumsPaginatedInteractor
import com.goupnorth.domain.interactors.RefreshAlbumsInteractor
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType
import com.goupnorth.leboncoin.ui.utils.setState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val refreshAlbumsInteractor: RefreshAlbumsInteractor,
    private val allAlbumsPaginatedInteractor: AllAlbumsPaginatedInteractor,
    private val savedState: SavedStateHandle
) : ViewModel() {

    data class State(
        val isLoading: Boolean,
        val isError: Boolean,
        val sort: Sort,
        val refresh: Boolean
    )

    sealed class UiModel {
        class UiAlbum(val album: Album) : UiModel()
        class Header(val sort: Sort) : UiModel()
    }

    data class Sort(val sortType: SortType, val sortOrder: SortOrder)

    private val _state = MutableStateFlow(
        State(
            isLoading = false,
            isError = false,
            sort = Sort(
                savedState.get<SortType>(SAVED_SORT_KEY) ?: SortType.SORT_BY_ID,
                savedState.get<SortOrder>(SAVED_ORDER_KEY) ?: SortOrder.ASC
            ),
            refresh = false
        )
    )
    val state = _state.asStateFlow()

    // Paging configuration
    private val config = PagingConfig(pageSize = 50, enablePlaceholders = false, maxSize = 200)
    val albums: Flow<PagingData<UiModel>> = Pager(config) {
        allAlbumsPaginatedInteractor.execute(state.value.sort.sortType, state.value.sort.sortOrder)
    }.flow.map {
        it.map { album -> UiModel.UiAlbum(album) }
            .insertSeparators { before, after ->
                when  {
                    before == null && after != null -> UiModel.Header(state.value.sort)
                    else -> null
                }
            }
    }.cachedIn(viewModelScope)

    fun loadAlbums() {
        viewModelScope.launch {
            try {
                _state.setState { copy(isLoading = true, isError = false) }
                refreshAlbumsInteractor.execute()
            } catch (t: Throwable) {
                _state.setState { copy(isError = true) }
            } finally {
                _state.setState { copy(isLoading = false) }
            }
        }
    }

    fun errorMessageShown() {
        _state.setState { copy(isError = false) }
    }

    fun sortAlbumsBy(type: SortType, order: SortOrder) {
        val sort = Sort(type, order)
        if (sort != state.value.sort) {
            _state.setState { copy(sort = sort, refresh = true) }
        }
    }

    fun pagingSourceRefreshed() {
        _state.setState { copy(refresh = false) }
    }

    /**
     * Save the state of the ViewModel in a SavedStateHandle
     */
    fun saveState() {
        savedState[SAVED_SORT_KEY] = state.value.sort.sortType
        savedState[SAVED_ORDER_KEY] = state.value.sort.sortOrder
    }

    companion object {
        private const val SAVED_SORT_KEY = "SAVED_SORT_KEY"
        private const val SAVED_ORDER_KEY = "SAVED_ORDER_KEY"
    }
}