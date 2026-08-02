package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType

/**
 * Состояние каркаса приложения.
 *
 * Пока в нём только выбранная категория услуг — её показывает футер навигационного
 * рейла («Репетитор · v1.0.0» в макете).
 */
@Immutable
data class MainScreenState(
    val serviceType: ServiceType? = null,
)
