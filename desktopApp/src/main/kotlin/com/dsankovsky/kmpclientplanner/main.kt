package com.dsankovsky.kmpclientplanner

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.request.crossfade
import com.dsankovsky.kmpclientplanner.data.AppDirectory
import com.dsankovsky.kmpclientplanner.di.platformModule
import com.dsankovsky.kmpclientplanner.di.repositoryModule
import com.dsankovsky.kmpclientplanner.di.uiModule
import com.dsankovsky.kmpclientplanner.di.useCasesModule
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import java.awt.Dimension

fun main() {
    startKoin {
        modules(platformModule, useCasesModule, repositoryModule, uiModule)
    }
    setUpImageLoader()
    application {
        // Макет свёрстан под окно 1360; минимум — ширина, при которой рейл 248
        // и двухпанельные экраны ещё помещаются (expanded по гайдлайну Adaptive).
        val windowState = rememberWindowState(width = 1360.dp, height = 900.dp)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Client Planner",
        ) {
            LaunchedEffect(Unit) { window.minimumSize = Dimension(940, 640) }
            App()
        }
    }
}

/**
 * Дисковый кэш картинок рядом с базой и настройками.
 *
 * По умолчанию Coil кладёт его в `FileSystem.SYSTEM_TEMPORARY_DIRECTORY/coil3_disk_cache`,
 * то есть в системный temp — мимо каталога приложения, который мы стараемся держать
 * самодостаточным.
 */
private fun setUpImageLoader() {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(AppDirectory.file("image_cache").absolutePath.toPath())
                    .build()
            }
            .build()
    }
}
