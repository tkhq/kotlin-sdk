package com.example.kotlin_demo_wallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_demo_wallet.databinding.ActivityMainBinding
import com.example.kotlin_demo_wallet.ui.auth.AuthBottomSheet
import com.example.kotlin_demo_wallet.ui.dashboard.DashboardFragment
import com.turnkey.core.TurnkeyContext
import com.turnkey.models.AuthState
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"


private fun Group.setVisible(visible: Boolean) {
    this.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
}
private fun android.view.View.setVisible(visible: Boolean) {
    this.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var dashboardAttached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authComponentButton.setOnClickListener {
            AuthBottomSheet.newInstance().show(supportFragmentManager, "auth")
        }
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch { TurnkeyContext.clearSession() }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    TurnkeyContext.authState.collect { state ->
                        render(state)
                    }
                }
            }
        }
    }

    private fun render(state: AuthState) {
        when (state) {
            AuthState.loading -> {
                binding.progress.setVisible(true)
                binding.groupUnauthed.setVisible(false)
                binding.groupAuthed.setVisible(false)
            }
            AuthState.unauthenticated -> {
                binding.progress.setVisible(false)
                binding.groupUnauthed.setVisible(true)
                binding.groupAuthed.setVisible(false)
                if (dashboardAttached) {
                    supportFragmentManager.findFragmentByTag("dashboard")
                        ?.let { supportFragmentManager.beginTransaction().remove(it).commitNowAllowingStateLoss() }
                    dashboardAttached = false
                }
            }
            AuthState.authenticated -> {
                binding.progress.setVisible(false)
                binding.groupUnauthed.setVisible(false)
                binding.groupAuthed.setVisible(true)
                attachDashboardIfNeeded()
            }
        }
    }

    private fun attachDashboardIfNeeded() {
        if (dashboardAttached) return
        supportFragmentManager.beginTransaction()
            .replace(R.id.dashboardContainer, DashboardFragment(), "dashboard")
            .commitNowAllowingStateLoss()
        dashboardAttached = true
    }
}
