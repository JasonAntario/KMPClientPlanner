package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_reference_or_result
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceTattooFieldsView(
    fields: ServiceSpecificFields.TattooServiceSpecificFields,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(Res.string.service_reference_or_result))
//        KufarImageViewPager(
//            uris = fields.images,
//            onDeleteImageCLick = {
//                onAction(ServiceDetailsScreenAction.TattooServiceAction.OnImageDeleteClicked(it))
//            },
//            onAddImages = {
//                onAction(ServiceDetailsScreenAction.TattooServiceAction.OnImagesAdded(it))
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
    }
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