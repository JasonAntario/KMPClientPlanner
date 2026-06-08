package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnlineSelectorView(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onChange: (Boolean) -> Unit
) {
    val options = remember { listOf(Res.string.client_offline, Res.string.client_online) }
    var selectedIndex by remember(isOnline) { mutableIntStateOf(if (isOnline) 1 else 0) }

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, res ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    onChange(index == 1)
                },
                selected = index == selectedIndex,
                label = { Text(stringResource(res), color = MaterialTheme.colorScheme.primary) }
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewOnline() {
    ClientPlannerTheme {
        OnlineSelectorView(false) {}
    }
}