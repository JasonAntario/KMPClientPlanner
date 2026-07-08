package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.components.BooleanSelectorView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.service_choose_format_lession
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditServiceEducationFieldsView(
    fields: ServiceSpecificFields.EducationServiceSpecificFields,
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
                text = stringResource(Res.string.service_choose_format_lession),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TypeChip(label = "Репетитор", icon = Icons.Rounded.School)
        }

        BooleanSelectorView(
            value = fields.isOnline,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            onChange = {
                onAction(AddEditServiceAction.EducationServiceAction.OnFormatChanged(it))
            }
        )
    }
}

@Composable
internal fun TypeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@PreviewLightDark
@Composable
private fun PreviewAddEditServiceTeacherFieldsView() {
    ClientPlannerTheme {
        AddEditServiceEducationFieldsView(
            fields = ServiceSpecificFields.EducationServiceSpecificFields(),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
