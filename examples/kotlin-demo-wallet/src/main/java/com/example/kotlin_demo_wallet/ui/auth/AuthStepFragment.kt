package com.example.kotlin_demo_wallet.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_demo_wallet.R
import com.example.kotlin_demo_wallet.databinding.ComponentAuthCardBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialSharedAxis
import com.turnkey.core.TurnkeyContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthStepFragment : Fragment(R.layout.component_auth_card) {

    private val vm: AuthSheetViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ComponentAuthCardBinding.bind(view)

        binding.acEmailInput.doOnTextChanged { text, _, _, _ ->
            vm.email.value = text?.toString().orEmpty()
        }

        binding.acSendOtpButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val ok = vm.sendOtpEmail(vm.email.value)
                if (ok) navigateToOtp()
                else binding.acEmailInputLayout.error = "Failed to send code"
            }
        }

        binding.acGoogleOAuthButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    TurnkeyContext.handleGoogleOAuth(
                        activity = requireActivity(),
                    )
                    withContext(Dispatchers.Main) { dismissSheetSafely() }
                } catch (t: Throwable) {
                    Log.e("AuthStepFragment", "Failed to handle Google OAuth", t)
                }
            }
        }

        binding.acAppleOAuthButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    TurnkeyContext.handleAppleOAuth(
                        activity = requireActivity(),
                    )
                    withContext(Dispatchers.Main) { dismissSheetSafely() }
                } catch (t: Throwable) {
                    Log.e("AuthStepFragment", "Failed to handle Apple OAuth", t)
                }
            }
        }

        binding.acTwitterXOAuthButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    TurnkeyContext.handleXOAuth(
                        activity = requireActivity(),
                    )
                    withContext(Dispatchers.Main) { dismissSheetSafely() }
                } catch (t: Throwable) {
                    Log.e("AuthStepFragment", "Failed to handle X OAuth", t)
                }
            }
        }

        binding.acDiscordOAuthButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    TurnkeyContext.handleDiscordOAuth(
                        activity = requireActivity(),
                    )
                    withContext(Dispatchers.Main) { dismissSheetSafely() }
                } catch (t: Throwable) {
                    Log.e("AuthStepFragment", "Failed to handle Discord OAuth", t)
                }
            }
        }

         binding.acLoginWithPasskeyButton.setOnClickListener {
             viewLifecycleOwner.lifecycleScope.launch {
                 try {
                     TurnkeyContext.loginWithPasskey(
                         activity = requireActivity()
                     )
                     withContext(Dispatchers.Main) { dismissSheetSafely() }
                 } catch (t: Throwable) {
                     Log.e("AuthStepFragment", "Failed to login with passkey", t)
                 }
             }
         }

        binding.acSignUpWithPasskeyButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    TurnkeyContext.signUpWithPasskey(
                        activity = requireActivity()
                    )
                    withContext(Dispatchers.Main) { dismissSheetSafely() }
                } catch (t: Throwable) {
                    Log.e("AuthStepFragment", "Failed to sign up with passkey", t)
                }
            }
        }
    }

    private fun navigateToOtp() {
        val forward = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val back    = MaterialSharedAxis(MaterialSharedAxis.X, false)

        parentFragment?.childFragmentManager?.commit {
            setReorderingAllowed(true)
            replace(R.id.sheet_container, OtpStepFragment())
            addToBackStack("otp")
        }

        // Also set fragment transitions for nicer Material motion
        enterTransition = forward
        exitTransition = back
        reenterTransition = back
        returnTransition = forward
    }

    private fun dismissSheetSafely() {
        val sheet = (parentFragment as? BottomSheetDialogFragment) ?: return
        // Always commit on main to avoid background-thread fragment ops.
        requireActivity().runOnUiThread {
            val fm = sheet.parentFragmentManager
            if (!sheet.isAdded) return@runOnUiThread
            if (fm.isStateSaved) sheet.dismissAllowingStateLoss() else sheet.dismiss()
        }
    }
}