package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.EducationServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields

fun EducationServiceSpecificFieldsDbModel.toEducationServiceSpecificFields() =
    ServiceSpecificFields.EducationServiceSpecificFields(
        id = id,
        serviceId = serviceId,
        homework = homework,
        isOnline = isOnline
    )

fun ServiceSpecificFields.EducationServiceSpecificFields.toEducationServiceSpecificFieldsDbModel() =
    EducationServiceSpecificFieldsDbModel(
        id = id,
        serviceId = serviceId,
        homework = homework,
        isOnline = isOnline
    )