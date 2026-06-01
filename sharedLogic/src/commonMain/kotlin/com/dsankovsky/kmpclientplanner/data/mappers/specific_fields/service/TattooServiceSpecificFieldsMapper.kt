package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.ImagesListDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.TattooServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields

fun TattooServiceSpecificFieldsDbModel.toTattooServiceSpecificFields() =
    ServiceSpecificFields.TattooServiceSpecificFields(
        id = id,
        serviceId = serviceId,
        images = images?.images ?: emptyList()
    )

fun ServiceSpecificFields.TattooServiceSpecificFields.toTattooServiceSpecificFieldsDbModel() =
    TattooServiceSpecificFieldsDbModel(
        id = id,
        serviceId = serviceId,
        images = ImagesListDbModel(
            images = images
        )
    )