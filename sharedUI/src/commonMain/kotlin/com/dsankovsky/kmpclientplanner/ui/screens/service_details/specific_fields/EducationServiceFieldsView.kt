package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_homework
import org.jetbrains.compose.resources.stringResource


@Composable
fun ServiceEducationFieldsView(
    fields: ServiceSpecificFields.EducationServiceSpecificFields,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(Res.string.service_homework),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        OutlinedTextField(
            value = fields.homework.orEmpty(),
            onValueChange = { onAction(ServiceDetailsScreenAction.EducationServiceAction.OnHomeworkChanged(it)) },
            label = {
                Text(stringResource(Res.string.service_homework))
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    }
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
