package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun ShortNameBoxView(
    modifier: Modifier = Modifier,
    text: String = ""
) {

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                MaterialTheme.colorScheme.primaryContainer
            )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }


}