package com.app.splitwell.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

/**
 * Calls [onPoll] every [intervalMs] for as long as this screen is actually
 * visible and in the foreground.
 *
 * Built on repeatOnLifecycle(RESUMED), so it automatically:
 * - pauses when the app is backgrounded
 * - resumes from a clean state when it comes back to the foreground
 * - stops completely when you navigate away (composable leaves composition)
 *
 * No manual start/stop bookkeeping needed — just drop this into any screen
 * composable alongside its existing load() call.
 */
@Composable
fun PollWhileVisible(intervalMs: Long = 5_000L, onPoll: suspend () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(intervalMs)
                onPoll()
            }
        }
    }
}