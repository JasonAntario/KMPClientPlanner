package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.SportClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.toServiceDateTimeList
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.toServiceDateTimeListDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields

fun SportClientSpecificFieldsDbModel.toSportClientSpecificFields() =
    ClientSpecificFields.SportClientSpecificFields(
        id = id,
        clientId = clientId,
        weight = weight?.toString(),
        isOnline = isOnline,
        lessonDateTimeList = lessonDateTimeList.toServiceDateTimeList(),
    )

fun ClientSpecificFields.SportClientSpecificFields.toSportClientSpecificFieldsDbModel() =
    SportClientSpecificFieldsDbModel(
        id = id,
        clientId = clientId,
        weight = weight?.toFloat(),
        isOnline = isOnline,
        lessonDateTimeList = lessonDateTimeList.toServiceDateTimeListDbModel(),
    )