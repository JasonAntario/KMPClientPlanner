package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Circular avatar showing a person's initials.
 *
 * Defaults to the "muted"/neutral look (primaryContainer bg, primary text) to stay
 * backwards-compatible with existing callers, but [backgroundColor] / [contentColor]
 * can be overridden to render an emphasized avatar (e.g. primary bg / onPrimary text).
 * Size is driven by the passed [modifier].
 */
@Composable
fun ShortNameBoxView(
    modifier: Modifier = Modifier,
    text: String = "",
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
