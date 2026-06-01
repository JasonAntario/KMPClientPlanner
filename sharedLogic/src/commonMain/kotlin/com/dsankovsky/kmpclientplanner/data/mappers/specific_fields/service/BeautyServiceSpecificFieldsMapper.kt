package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.BeautyServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.ImagesListDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields

fun BeautyServiceSpecificFieldsDbModel.toBeautyServiceSpecificFields() =
    ServiceSpecificFields.BeautyServiceSpecificFields(
        id = id,
        serviceId = serviceId,
        images = images?.images ?: emptyList()
    )

fun ServiceSpecificFields.BeautyServiceSpecificFields.toBeautyServiceSpecificFieldsDbModel() =
    BeautyServiceSpecificFieldsDbModel(
        id = id,
        serviceId = serviceId,
        images = ImagesListDbModel(
            images = images
        )
    )