package com.dsankovsky.kmpclientplanner.ui.screens.client_details.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsActions
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_current_project
import kmpclientplanner.sharedui.generated.resources.client_finish_project
import kmpclientplanner.sharedui.generated.resources.client_finished_projects
import org.jetbrains.compose.resources.stringResource

@Composable
fun TattooClientFieldsView(
    fields: ClientSpecificFields.TattooClientSpecificFields,
    onAction: (ClientDetailsActions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(Res.string.client_current_project),
        )
//        KufarImageViewPager(
//            uris = fields.currentProject.imageUrls,
//            onDeleteImageCLick = {
//                onAction(ClientDetailsActions.TattooClientAction.OnImageDeleteClicked(it))
//            },
//            onAddImages = {
//                onAction(ClientDetailsActions.TattooClientAction.OnImagesAdded(it))
//            }
//        )

        TextButton(
            onClick = {
                onAction(ClientDetailsActions.TattooClientAction.OnFinishProjectClicked)
            },
            enabled = fields.currentProject.imageUrls.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.client_finish_project))
        }

        if (fields.finishedProjects.isNotEmpty()) {
            Text(
                stringResource(Res.string.client_finished_projects),
            )
        }

//        fields.finishedProjects.forEachIndexed { index, tattooProject ->
//            KufarImageViewPager(
//                uris = tattooProject.imageUrls,
//                onDeleteImageCLick = {
//                    onAction(
//                        ClientDetailsActions.TattooClientAction.OnImageDeleteClickedInFinishedProject(
//                            it,
//                            index
//                        )
//                    )
//                },
//                onAddImages = {
//                    onAction(
//                        ClientDetailsActions.TattooClientAction.OnImagesAddedInFinishedProject(
//                            it,
//                            index
//                        )
//                    )
//                }
//            )
//        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewAddEditClientScreenTattoo() {
    ClientPlannerTheme {
        TattooClientFieldsView(
            fields = ClientSpecificFields.TattooClientSpecificFields(
                finishedProjects = listOf(
                    ClientSpecificFields.TattooClientSpecificFields.TattooProject(
                        listOf("asfdsf")
                    )
                )
            ),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}