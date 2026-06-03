package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_homework
import org.jetbrains.compose.resources.stringResource


@Composable
fun ServiceEducationFieldsView(
    fields: ServiceSpecificFields.EducationServiceSpecificFields,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val homework = rememberTextFieldState(fields.homework.orEmpty())

    OutlinedTextField(
        state = homework,
        label = {
            Text(stringResource(Res.string.service_homework))
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )
}


@PreviewLightDark
@Composable
private fun PreviewAddEditServiceTeacherFieldsView() {
    ClientPlannerTheme {
        ServiceEducationFieldsView(
            fields = ServiceSpecificFields.EducationServiceSpecificFields(),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}