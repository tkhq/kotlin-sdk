package com.example.kotlin_demo_wallet.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_demo_wallet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtpStepFragment : Fragment(R.layout.component_step_otp) {

    private val vm: AuthSheetViewModel by viewModels({ requireParentFragment() })

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
        val verify = view.findViewById<Button>(R.id.otpVerify)
        val back = view.findViewById<Button>(R.id.otpBack)

        input.doOnTextChanged { text, _, _, _ ->
            verify.isEnabled = (text?.length ?: 0) >= 6
        }

        verify.setOnClickListener {
            val code = input.text?.toString().orEmpty()
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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
    }
}
