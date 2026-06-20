package com.dsankovsky.kmpclientplanner.uinew.android

import androidx.compose.runtime.Composable
import com.dsankovsky.kmpclientplanner.App

/**
 * Root of the new Android UI layer (`uinew/android`).
 *
 * Placeholder scaffold: the dedicated Android (MVP) screens come from the
 * "Lessons App — Android (MVP)" design in a follow-up. For now it delegates to the
 * existing [App] so the Android target keeps working and the package structure exists.
 */
@Composable
fun AndroidApp() {
    App()
}
