package com.example.splitbuddy.ui

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.splitbuddy.data.sync.AppLifecycleObserver
import com.example.splitbuddy.di.DIContainer
import org.koin.core.context.GlobalContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DIContainer.main(this)

        // Get AppLifecycleObserver from Koin after modules are loaded
        val lifecycleObserver = GlobalContext.get().get<AppLifecycleObserver>()

        // Register to observe app foreground/background
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }
}