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
        bringAppToFront()
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
        val pm = applicationContext.packageManager
        val pkg = applicationContext.packageName

        // Try the default launch intent first
        val launch: Intent? = pm.getLaunchIntentForPackage(pkg)?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
        } ?: run {
            // Fallback: resolve a launcher activity inside this package
            val main = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(pkg)
            }
            val ri = pm.queryIntentActivities(main, 0).firstOrNull()
            ri?.let {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setClassName(it.activityInfo.packageName, it.activityInfo.name)
                    addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }
            }
        }

        if (launch != null) {
            startActivity(launch)
        } else {
            Log.w("OAuthRedirect", "No launchable activity found for package: $pkg")
        }
    }
}
