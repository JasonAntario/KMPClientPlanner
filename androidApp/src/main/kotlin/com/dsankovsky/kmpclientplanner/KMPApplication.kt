package com.dsankovsky.kmpclientplanner

import android.app.Application
import com.dsankovsky.kmpclientplanner.di.platformModule
import com.dsankovsky.kmpclientplanner.di.repositoryModule
import com.dsankovsky.kmpclientplanner.di.uiModule
import com.dsankovsky.kmpclientplanner.di.useCasesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KMPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KMPApplication)
            modules(platformModule, useCasesModule, repositoryModule, uiModule)
        }
    }
}
