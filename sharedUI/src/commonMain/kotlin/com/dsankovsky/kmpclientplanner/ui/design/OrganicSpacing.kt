package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Шкала отступов Organic (`--space-1..8`) с уже вшитой плотностью 1.10×.
 * Макет свёрстан под окно 1360 px, десктоп рисует 1 CSS px = 1 dp, поэтому
 * дробные значения переносим как есть.
 */
@Immutable
data class OrganicSpacing(
    val space1: Dp = 4.4.dp,
    val space2: Dp = 8.8.dp,
    val space3: Dp = 13.2.dp,
    val space4: Dp = 17.6.dp,
    val space6: Dp = 26.4.dp,
    val space8: Dp = 35.2.dp,
)
