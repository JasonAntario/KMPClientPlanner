package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.TattooClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.ImagesListDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields

fun TattooClientSpecificFieldsDbModel.toTattooClientSpecificFields() =
    ClientSpecificFields.TattooClientSpecificFields(
        id = id,
        clientId = clientId,
        currentProject = currentProject?.toTattooProject()
            ?: ClientSpecificFields.TattooClientSpecificFields.TattooProject(),
        finishedProjects = finishedProjects?.projects?.map { it.toTattooProject() } ?: emptyList()
    )

fun TattooClientSpecificFieldsDbModel.TattooProjectDbModel.toTattooProject() =
    ClientSpecificFields.TattooClientSpecificFields.TattooProject(
        imageUrls = images?.images ?: emptyList()
    )

fun ClientSpecificFields.TattooClientSpecificFields.toTattooClientSpecificFieldsDbModel(): TattooClientSpecificFieldsDbModel {
    val currentProject = if (currentProject.imageUrls.isEmpty()) {
        null
    } else {
        currentProject.toTattooProjectDbModel()
    }

    val finishedProjects = if (finishedProjects.isEmpty()) {
        null
    } else {
        TattooClientSpecificFieldsDbModel.TattooProjectsListDbModel(finishedProjects.map { it.toTattooProjectDbModel() })
    }

    return TattooClientSpecificFieldsDbModel(
        id = id,
        clientId = clientId,
        currentProject = currentProject,
        finishedProjects = finishedProjects
    )
}

fun ClientSpecificFields.TattooClientSpecificFields.TattooProject.toTattooProjectDbModel() =
    TattooClientSpecificFieldsDbModel.TattooProjectDbModel(
        images = ImagesListDbModel(images = imageUrls)
    )