package com.dsankovsky.kmpclientplanner.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ExpandShrinkAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    expandFrom: Alignment.Vertical = Alignment.Bottom,
    shrinkTowards: Alignment.Vertical = Alignment.Bottom,
    label: String = "",
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = expandVertically(expandFrom = expandFrom),
        exit = shrinkVertically(shrinkTowards = shrinkTowards),
        label = label,
        content = content
    )
}