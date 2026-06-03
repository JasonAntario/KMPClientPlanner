package com.dsankovsky.kmpclientplanner.ui.screens.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.start
import kmpclientplanner.sharedui.generated.resources.welcome_desctription
import kmpclientplanner.sharedui.generated.resources.welcome_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .edgeToEdgeBottomPadding(0.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            text = stringResource(Res.string.welcome_message),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(Res.string.welcome_desctription),
        )
        Spacer(Modifier.weight(1f))
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartClick
        ) {
            Text(stringResource(Res.string.start))
        }
    }
}

@PreviewLightDark
@Composable
private fun PreveiwWelcomeScreen() {
    ClientPlannerTheme {
        WelcomeScreen() {}
    }
}