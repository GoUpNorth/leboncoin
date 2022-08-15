package com.goupnorth.leboncoin.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType
import com.goupnorth.leboncoin.R
import com.goupnorth.leboncoin.databinding.AlbumsFragmentBinding
import com.goupnorth.leboncoin.ui.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumsFragment : Fragment() {

    private var _binding: AlbumsFragmentBinding? = null
    private val binding: AlbumsFragmentBinding
        get() = _binding!!

    private val viewModel: AlbumsViewModel by viewModels()

    private lateinit var adapter: AlbumsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSortPickerListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlbumsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AlbumsAdapter().apply {
            // Setup the list sort picker listener
            onSortButtonClick = this@AlbumsFragment::showSortPicker
            addOnPagesUpdatedListener { showState(viewModel.state.value) }
        }

        with(binding.recyclerView) {
            adapter = this@AlbumsFragment.adapter
            // Since the RecyclerView takes the whole screen, some optimization can be done
            setHasFixedSize(true)
            // Add spacing between the ViewHolders with an ItemDecoration, rather than in the ViewHolder xml layout
            addItemDecoration(MarginItemDecoration(8.dp))
        }

        binding.retryButton.setOnClickListener { viewModel.loadAlbums() }

        // Use the viewLifecycleOwner.lifecycleScope to bind the coroutines to the lifecycle of the fragment's view
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { showState(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.albums.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAlbums()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null // unbind the adapter here to avoid a memory leak
        _binding = null
    }

    private fun showState(state: AlbumsViewModel.State) {
        // Progress bar
        binding.progressBar.isVisible = state.isLoading

        // Error ui
        Log.i("Test bug", "isError  = ${state.isError} && itemCount =  ${adapter.itemCount}")
        binding.retryView.isVisible = state.isError && adapter.itemCount == 0
        if (state.isError && adapter.itemCount > 0) {
            Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_SHORT).show()
            viewModel.errorMessageShown()
        }

        // if need to refresh the pagingSource
        if (state.refresh) {
            adapter.refresh()
            viewModel.pagingSourceRefreshed()
        }
    }

    private fun showSortPicker(currentSort: AlbumsViewModel.Sort) {
        SortPickerBottomSheetDialog.newInstance(currentSort.sortType, currentSort.sortOrder)
            .show(childFragmentManager, null)
    }

    private fun setupSortPickerListener() {
        childFragmentManager.setFragmentResultListener(
            SortPickerBottomSheetDialog.SORT_RESULT_REQUEST_KEY,
            this
        ) { _, bundle ->
            val sort = bundle.getSerializable(SortPickerBottomSheetDialog.TYPE_KEY) as? SortType
            val order = bundle.getSerializable(SortPickerBottomSheetDialog.ORDER_KEY) as? SortOrder
            if (sort != null && order != null) {
                viewModel.sortAlbumsBy(sort, order)
            }
        }
    }
}