package com.dsankovsky.kmpclientplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlin.reflect.KClass

typealias NavBackStack = SnapshotStateList<Any>

@Composable
fun rememberNavBackStack(initialRoute: Any): NavBackStack =
    remember { mutableListOf(initialRoute).toMutableStateList() }

class EntryProviderScope {
    @PublishedApi
    internal val entries = mutableMapOf<KClass<*>, @Composable () -> Unit>()

    inline fun <reified T : Any> entry(noinline content: @Composable () -> Unit) {
        entries[T::class] = content
    }
}

fun entryProvider(builder: EntryProviderScope.() -> Unit): Map<KClass<*>, @Composable () -> Unit> =
    EntryProviderScope().apply(builder).entries

@Composable
fun NavDisplay(
    backStack: NavBackStack,
    @Suppress("UNUSED_PARAMETER") onBack: () -> Unit = {},
    entryProvider: Map<KClass<*>, @Composable () -> Unit>
) {
    val currentRoute = backStack.lastOrNull() ?: return
    entryProvider[currentRoute::class]?.invoke()
}
