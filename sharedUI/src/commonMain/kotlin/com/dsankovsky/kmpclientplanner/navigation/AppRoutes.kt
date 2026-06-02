package com.dsankovsky.kmpclientplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class AppRoutes : NavKey {

    @Serializable
    data object WelcomeRoute : AppRoutes()

    @Serializable
    data object MainRoute : AppRoutes()
}
