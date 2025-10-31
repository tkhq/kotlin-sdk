package com.turnkey.core

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class OAuthRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handle(intent)
        bringAppToFront()
        finish() // return to previous activity
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
        finish() // ensure we pop back immediately
    }

    private fun handle(intent: Intent?) {
        val uri: Uri = intent?.data ?: run {
            Log.w("OAuthRedirect", "No data on redirect intent")
            return
        }
        Log.d("OAuthRedirect", "Received redirect: $uri")
        OAuthEvents.deepLinks.tryEmit(uri)
    }

    private fun bringAppToFront() {
        // Launch host app's main activity on top of the Custom Tab
        val launch = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }
        if (launch != null) {
            startActivity(launch)
        }
    }
}
