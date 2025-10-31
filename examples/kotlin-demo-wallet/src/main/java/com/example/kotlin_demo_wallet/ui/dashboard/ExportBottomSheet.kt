package com.example.kotlin_demo_wallet.ui.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.example.kotlin_demo_wallet.R
import com.example.kotlin_demo_wallet.databinding.ComponentExportBinding
import com.example.kotlin_demo_wallet.databinding.ComponentSignatureBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class ExportBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ComponentExportBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_RESULT = "arg_result"

        fun newInstance(resultText: String): ExportBottomSheet =
            ExportBottomSheet().apply {
                arguments = bundleOf(ARG_RESULT to resultText)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ComponentExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val result = arguments?.getString(ARG_RESULT).orEmpty()
        binding.tvResult.text = result
        binding.tvResult.setTextIsSelectable(true)

        binding.tvResult.setOnClickListener {
            val cm =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("signature", result))
            Snackbar.make(binding.root, "Copied to clipboard", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        val sheet =
            dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        sheet?.let {
            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
        dialog?.window?.setDimAmount(0.2f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

