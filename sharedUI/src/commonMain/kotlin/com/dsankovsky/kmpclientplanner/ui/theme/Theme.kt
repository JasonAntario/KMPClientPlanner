package com.dsankovsky.kmpclientplanner.ui.theme

import androidx.compose.runtime.Composable
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme

/**
 * Переходный алиас на [OrganicTheme]: остаётся, пока на него ссылаются `@Preview`
 * ещё не переписанных экранов. Удаляется вместе с последним старым экраном (этап 6).
 */
@Composable
fun ClientPlannerTheme(content: @Composable () -> Unit) {
    OrganicTheme(content = content)
}
