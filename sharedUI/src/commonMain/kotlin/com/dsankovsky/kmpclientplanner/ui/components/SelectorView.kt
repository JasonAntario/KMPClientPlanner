package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
fun BooleanSelectorView(
    value: Boolean,
    falseLabel: String,
    trueLabel: String,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = remember(falseLabel, trueLabel) { listOf(falseLabel, trueLabel) }
    var selectedIndex by remember(value) { mutableIntStateOf(if (value) 1 else 0) }

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex = index
                    onChange(index == 1)
                },
                selected = index == selectedIndex,
                label = { Text(label, color = MaterialTheme.colorScheme.primary) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewBooleanSelectorView() {
    ClientPlannerTheme {
        BooleanSelectorView(
            value = false,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            onChange = {}
        )
    }
}
