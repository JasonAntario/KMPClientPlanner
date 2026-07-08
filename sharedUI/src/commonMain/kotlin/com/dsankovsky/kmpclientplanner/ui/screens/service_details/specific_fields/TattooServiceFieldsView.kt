package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_reference_or_result
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceTattooFieldsView(
    fields: ServiceSpecificFields.TattooServiceSpecificFields,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    ReferenceImagesSection(
        title = stringResource(Res.string.service_reference_or_result),
        icon = Icons.Rounded.Brush,
        images = fields.images,
        modifier = modifier
    )
}


@PreviewLightDark
@Composable
private fun PreviewAddEditServiceTattooFieldsView() {
    ClientPlannerTheme {
        ServiceTattooFieldsView(
            fields = ServiceSpecificFields.TattooServiceSpecificFields(images = listOf("dasda")),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
