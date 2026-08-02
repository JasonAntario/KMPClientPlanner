package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme

/**
 * Подложка для `@Preview` атомов: тема Organic, кремовый фон и воздух вокруг.
 * Ширина задаётся явно — большинство контролов тянется на всю доступную.
 */
@Composable
internal fun PreviewSurface(
    width: Dp = Dp.Unspecified,
    content: @Composable ColumnScope.() -> Unit,
) {
    OrganicTheme {
        Column(
            modifier = Modifier
                .background(OrganicTheme.colors.bg)
                .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier)
                .padding(OrganicTheme.spacing.space4),
            verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
            content = content,
        )
    }
}
