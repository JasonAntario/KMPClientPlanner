package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditServiceEducationFieldsView(
    fields: ServiceSpecificFields.EducationServiceSpecificFields,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {

    BooleanSelectorView(
        modifier = modifier,
        value = fields.isOnline,
        falseLabel = stringResource(Res.string.client_offline),
        trueLabel = stringResource(Res.string.client_online),
        onChange = {
            onAction(AddEditServiceAction.EducationServiceAction.OnFormatChanged(it))
        }
    )
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