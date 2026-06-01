package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.EducationClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.toServiceDateTimeList
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.toServiceDateTimeListDbModel

fun EducationClientSpecificFieldsDbModel.toEducationClientSpecificFields() =
    ClientSpecificFields.EducationClientSpecificFields(
        id = id,
        clientId = clientId,
        level = level,
        isOnline = isOnline,
        lessonDateTimeList = lessonDateTimeList.toServiceDateTimeList(),
    )


fun ClientSpecificFields.EducationClientSpecificFields.toEducationClientSpecificFieldsDbModel() =
    EducationClientSpecificFieldsDbModel(
        id = id,
        clientId = clientId,
        level = level,
        isOnline = isOnline,
        lessonDateTimeList = lessonDateTimeList.toServiceDateTimeListDbModel()
    )