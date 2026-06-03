package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme


@Composable
fun AddEditServiceEducationFieldsView(
    fields: ServiceSpecificFields.EducationServiceSpecificFields,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {
//    KufarOnlineSelector(
//        serviceType = ServiceType.EDUCATION,
//        isOnline = fields.isOnline,
//        modifier = modifier.padding(bottom = 8.dp)
//    ) {
//        onAction(AddEditServiceAction.EducationServiceAction.OnFormatChanged(it))
//    }
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