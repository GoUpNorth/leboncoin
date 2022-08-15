package com.goupnorth.leboncoin.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goupnorth.domain.models.SortOrder
import com.goupnorth.domain.models.SortType
import com.goupnorth.leboncoin.databinding.SortPickerBinding

/**
 * Bottom sheet that allows to pick sort type and order.
 * To be notified of the selected sort and order, the parent fragment has to register a FragmentResultListener
 * on its childFragmentManager with the key [SortPickerBottomSheetDialog.SORT_RESULT_REQUEST_KEY]
 * The selected sort can be retrieved from the bundle as a [Serializable] of [com.goupnorth.domain.models.SortType] with the key [SortPickerBottomSheetDialog.TYPE_KEY]
 * The selected order can be retrieved from the bundle as a [Serializable] of [com.goupnorth.domain.models.SortOrder] with the key [SortPickerBottomSheetDialog.ORDER_KEY]
 */
class SortPickerBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: SortPickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SortPickerBinding.inflate(inflater, container, false)

        dialog?.setOnShowListener { expandBottomSheetDialog(it as BottomSheetDialog) }

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup listeners
        _binding!!.sortByIdAsc.setOnClickListener {
            setResult(SortType.SORT_BY_ID, SortOrder.ASC)
        }
        _binding!!.sortByIdDesc.setOnClickListener {
            setResult(SortType.SORT_BY_ID, SortOrder.DESC)
        }
        _binding!!.sortByTitleAsc.setOnClickListener {
            setResult(SortType.SORT_BY_TITLE, SortOrder.ASC)
        }
        _binding!!.sortByTitleDesc.setOnClickListener {
            setResult(SortType.SORT_BY_TITLE, SortOrder.DESC)
        }

        // setup initial sort
        val initialType = requireArguments().get(TYPE_KEY) as SortType
        val initialOrder = requireArguments().get(ORDER_KEY) as SortOrder
        when {
            initialType == SortType.SORT_BY_ID && initialOrder == SortOrder.ASC -> {
                _binding!!.sortByIdAsc.isChecked = true
            }
            initialType == SortType.SORT_BY_ID && initialOrder == SortOrder.DESC -> {
                _binding!!.sortByIdDesc.isChecked = true
            }
            initialType == SortType.SORT_BY_TITLE && initialOrder == SortOrder.ASC -> {
                _binding!!.sortByTitleAsc.isChecked = true
            }
            initialType == SortType.SORT_BY_TITLE && initialOrder == SortOrder.DESC -> {
                _binding!!.sortByTitleDesc.isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setResult(type: SortType, order: SortOrder) {
        setFragmentResult(
            SORT_RESULT_REQUEST_KEY,
            bundleOf(
                TYPE_KEY to type,
                ORDER_KEY to order
            )
        )
        dismiss()
    }

    private fun expandBottomSheetDialog(dialog: BottomSheetDialog) {
        val view = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        BottomSheetBehavior.from(view!!).state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        const val SORT_RESULT_REQUEST_KEY = "SORT_RESULT_REQUEST_KEY"
        const val TYPE_KEY = "TYPE_KEY"
        const val ORDER_KEY = "ORDER_KEY"

        fun newInstance(initialSort: SortType, initialOrder: SortOrder) =
            SortPickerBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(TYPE_KEY, initialSort)
                    putSerializable(ORDER_KEY, initialOrder)
                }
            }
    }
}