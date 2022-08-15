package com.goupnorth.leboncoin.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.goupnorth.domain.models.Album
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType
import com.goupnorth.leboncoin.R
import com.goupnorth.leboncoin.databinding.AlbumViewHolderBinding
import com.goupnorth.leboncoin.databinding.SortHeaderBinding
import com.goupnorth.leboncoin.ui.utils.capitalize

class AlbumsAdapter :
    PagingDataAdapter<AlbumsViewModel.UiModel, RecyclerView.ViewHolder>(diffCallback) {

    var onSortButtonClick: (AlbumsViewModel.Sort) -> Unit = { }

    override fun getItemViewType(position: Int): Int {
        return when (peek(position)) {
            is AlbumsViewModel.UiModel.Header -> HEADER_VIEW_TYPE
            is AlbumsViewModel.UiModel.UiAlbum -> ALBUM_VIEW_TYPE
            null -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> HeaderViewHolder.create(parent)
            else -> AlbumViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(
                (model as AlbumsViewModel.UiModel.Header).sort,
                onSortButtonClick
            )
            is AlbumViewHolder -> holder.bind((model as AlbumsViewModel.UiModel.UiAlbum).album)
        }
    }

    class AlbumViewHolder private constructor(private val binding: AlbumViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) = with(binding) {
            titleTextView.text =
                String.format("%d - %s", album.id, album.title.capitalize())

            Glide.with(root)
                .load(album.thumbnailUrl)
                .transition(DrawableTransitionOptions.withCrossFade(150))
                .placeholder(R.drawable.ic_audio_24dp)
                .into(thumbnail)
        }

        companion object {
            fun create(parent: ViewGroup): AlbumViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = AlbumViewHolderBinding.inflate(inflater, parent, false)
                return AlbumViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder private constructor(private val binding: SortHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sort: AlbumsViewModel.Sort, onClick: (AlbumsViewModel.Sort) -> Unit) {
            val context = binding.root.context
            val stringId = when (sort) {
                AlbumsViewModel.Sort(SortType.SORT_BY_ID, SortOrder.ASC) -> R.string.sort_by_id_asc
                AlbumsViewModel.Sort(
                    SortType.SORT_BY_ID,
                    SortOrder.DESC
                ) -> R.string.sort_by_id_desc
                AlbumsViewModel.Sort(
                    SortType.SORT_BY_TITLE,
                    SortOrder.ASC
                ) -> R.string.sort_by_title_asc
                AlbumsViewModel.Sort(
                    SortType.SORT_BY_TITLE,
                    SortOrder.DESC
                ) -> R.string.sort_by_title_desc
                else -> throw IllegalStateException()
            }
            with(binding.sort) {
                text = context.getString(stringId)
                setOnClickListener { onClick(sort) }
            }
        }

        companion object {
            fun create(parent: ViewGroup): HeaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = SortHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<AlbumsViewModel.UiModel>() {

            override fun areItemsTheSame(
                oldItem: AlbumsViewModel.UiModel,
                newItem: AlbumsViewModel.UiModel
            ): Boolean {
                return (oldItem is AlbumsViewModel.UiModel.Header && newItem is AlbumsViewModel.UiModel.Header) ||
                        (oldItem as? AlbumsViewModel.UiModel.UiAlbum)?.album?.id == (newItem as? AlbumsViewModel.UiModel.UiAlbum)?.album?.id
            }

            override fun areContentsTheSame(
                oldItem: AlbumsViewModel.UiModel,
                newItem: AlbumsViewModel.UiModel
            ): Boolean {
                return if (oldItem is AlbumsViewModel.UiModel.Header && newItem is AlbumsViewModel.UiModel.Header) {
                    oldItem.sort == newItem.sort
                } else if (oldItem is AlbumsViewModel.UiModel.UiAlbum && newItem is AlbumsViewModel.UiModel.UiAlbum) {
                    oldItem.album == newItem.album
                } else {
                    false
                }
            }
        }

        private const val HEADER_VIEW_TYPE = 111
        private const val ALBUM_VIEW_TYPE = 112
    }
}