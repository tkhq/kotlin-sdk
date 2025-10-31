package com.example.kotlin_demo_wallet.ui.auth

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.fragment.app.commit
import com.example.kotlin_demo_wallet.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AuthBottomSheet : BottomSheetDialogFragment() {

    companion object { fun newInstance() = AuthBottomSheet() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bs_auth_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.sheet_container, AuthStepFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!childFragmentManager.popBackStackImmediate()) dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                peekHeight = (resources.displayMetrics.heightPixels * 0.5f).toInt()
            }
        }
        dialog?.window?.apply {
            setDimAmount(0.1f)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}

