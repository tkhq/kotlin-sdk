package com.example.kotlin_demo_wallet.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_demo_wallet.R
import com.google.android.material.textview.MaterialTextView
import com.turnkey.core.TurnkeyContext
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val tvGreeting = view.findViewById<MaterialTextView>(R.id.tvGreeting)
//        val tvSessionKeyShort = view.findViewById<MaterialTextView>(R.id.tvSessionKeyShort)

//        tvGreeting.text = "Dashboard"

        // Optional: reflect current session key in UI
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    TurnkeyContext.selectedSessionKey.collect { key ->
//                        tvSessionKeyShort.text =
//                            if (key.isNullOrBlank()) "Session: —"
//                            else "Session: ${key.truncateMiddle(8)}"
//                    }
//                }
//            }
//        }
    }
}

private fun String.truncateMiddle(visibleEachSide: Int): String {
    if (length <= visibleEachSide * 2 + 3) return this
    val start = substring(0, visibleEachSide)
    val end = substring(length - visibleEachSide, length)
    return "$start…$end"
}