package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.components.BooleanSelectorView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.service_choose_format_training
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditServiceSportFieldsView(
    fields: ServiceSpecificFields.SportServiceSpecificFields,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.service_choose_format_training),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TypeChip(label = "Тренер", icon = Icons.Rounded.FitnessCenter)
        }

        BooleanSelectorView(
            value = fields.isOnline,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            onChange = {
                onAction(AddEditServiceAction.SportServiceAction.OnFormatChanged(it))
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddEditServiceSportFieldsView() {
    ClientPlannerTheme {
        AddEditServiceSportFieldsView(
            modifier = Modifier.padding(16.dp),
            fields = ServiceSpecificFields.SportServiceSpecificFields(),
            onAction = {}
        )
    }
}
