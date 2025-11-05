package com.example.kotlin_demo_wallet.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_demo_wallet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OtpStepFragment : Fragment(R.layout.component_step_otp) {

    private val vm: AuthSheetViewModel by viewModels({ requireParentFragment() })

    private var verifyOriginalText: CharSequence? = null
    private var spinnerDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fwd = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val bwd = MaterialSharedAxis(MaterialSharedAxis.X, false)
        enterTransition = fwd
        returnTransition = bwd
        reenterTransition = bwd
        exitTransition = fwd
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val input = view.findViewById<EditText>(R.id.otpInput)
        val verify = view.findViewById<MaterialButton>(R.id.otpVerify)
        val back = view.findViewById<MaterialButton>(R.id.otpBack)

        val spec = CircularProgressIndicatorSpec(
            requireContext(), /* attrs = */ null, /* defStyleAttr = */ 0,
            com.google.android.material.R.style
                .Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        spinnerDrawable = IndeterminateDrawable.createCircularDrawable(requireContext(), spec)

        input.doOnTextChanged { text, _, _, _ ->
            verify.isEnabled = (text?.length ?: 0) >= 6
        }

        verify.setOnClickListener {
            if (vm.loading.value) return@setOnClickListener
            val code = input.text?.toString().orEmpty()
            viewLifecycleOwner.lifecycleScope.launch {
                val ok = vm.verifyOtp(code)
                if (ok) {
                    (parentFragment as? BottomSheetDialogFragment)?.dismiss()
                } else {
                    input.error = "Invalid or expired code"
                }
            }
        }

        back.setOnClickListener {
            parentFragment?.childFragmentManager?.popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.loading.collectLatest { isLoading ->
                    setVerifyLoading(verify, input, isLoading)
                }
            }
        }
    }

    private fun setVerifyLoading(
        verify: MaterialButton,
        input: EditText,
        loading: Boolean
    ) {
        if (loading) {
            if (verifyOriginalText == null) verifyOriginalText = verify.text
            verify.text = null
            verify.icon = spinnerDrawable
            verify.iconPadding = 0
            verify.isEnabled = false
            input.isEnabled = false
        } else {
            verify.icon = null
            verify.text = verifyOriginalText ?: "Verify"
            verify.isEnabled = (input.text?.length ?: 0) >= 6
            input.isEnabled = true
        }
    }
}
