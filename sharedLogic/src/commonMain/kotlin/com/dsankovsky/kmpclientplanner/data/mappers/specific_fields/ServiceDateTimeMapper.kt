package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.ServiceDateTimeDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.ServiceDateTimeListDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.extensions.getDayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

fun ServiceDateTimeListDbModel.toServiceDateTimeList(): List<ServiceDateTime> {
    return list.map {
        ServiceDateTime(
            dayOfWeek = getDayOfWeek(it.dayOfWeekIsoIndex),
            time = LocalTime.fromSecondOfDay(it.timeInSeconds),
            duration = it.duration.toString()
        )
    }
}

fun List<ServiceDateTime>.toServiceDateTimeListDbModel(): ServiceDateTimeListDbModel {
    val list = this.map {
        ServiceDateTimeDbModel(
            dayOfWeekIsoIndex = it.dayOfWeek.isoDayNumber,
            timeInSeconds = it.time.toSecondOfDay(),
            duration = it.duration.toFloat()
        )
    }
    return ServiceDateTimeListDbModel(list)
}