package com.turnkey.core

import android.app.Application
import com.turnkey.models.TurnkeyConfig
import kotlinx.coroutines.*

object TurnkeyCore {
    lateinit var ctx: TurnkeyContext
        private set

    // internal scope tied to process lifetime
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main.immediate + job)

    /** Completes when ctx.init() finishes (if callers want to await it). */
    val ready: Deferred<Unit> = CompletableDeferred()

    fun init(app: Application, config: TurnkeyConfig) {
        ctx = TurnkeyContext(app, config)

        scope.launch {
            try {
                ctx.init()              // this is suspend, so run it here
                (ready as CompletableDeferred).complete(Unit)
            } catch (t: Throwable) {
                (ready as CompletableDeferred).completeExceptionally(t)
                throw t
            }
        }
    }

    /** Optional: call from Application.onTerminate() to clean up (tests/emulators). */
    fun shutdown() {
        job.cancel()
    }
}
