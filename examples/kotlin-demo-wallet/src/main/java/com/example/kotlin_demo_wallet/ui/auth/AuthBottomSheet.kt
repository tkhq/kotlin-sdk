package com.example.kotlin_demo_wallet.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_demo_wallet.TurnkeyHelpers
import com.example.kotlin_demo_wallet.databinding.ComponentAuthCardBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.turnkey.passkey.PasskeyUser
import com.turnkey.passkey.RelyingParty
import com.turnkey.passkey.createPasskey
import kotlinx.coroutines.launch

class AuthBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ComponentAuthCardBinding? = null
    private val binding get() = _binding!!

    private var email: String = ""

    companion object {
        fun newInstance(): AuthBottomSheet = AuthBottomSheet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComponentAuthCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.acEmailInput.doOnTextChanged { text, start, before, count ->

        }

        binding.acLoginWithPasskeyButton.setOnClickListener {
            lifecycleScope.launch {
                val res = createPasskey(
                    activity = requireActivity(),
                    user = PasskeyUser(
                        "564eb2d1-805e-434d-962a-927096caaf49",
                        "Passkey",
                        "Passkey"
                    ),
                    rp = RelyingParty(
                        id = "https://bb687dc33333.ngrok-free.app/ethankonk.github.io/",
                        name = "kotlin_demo_app"
                    )
                )
                println(res)
            }
        }
    }


    override fun onStart() {

        super.onStart()

        val sheet =
            dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        sheet.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = (resources.displayMetrics.heightPixels * 0.5f).toInt()
        }

        dialog?.window?.setDimAmount(0.1f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
