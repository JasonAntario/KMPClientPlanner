package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients

import kotlinx.serialization.Serializable


@Serializable
data class ServiceDateTimeListDbModel(
    val list: List<ServiceDateTimeDbModel>
)

@Serializable
data class ServiceDateTimeDbModel(
    val dayOfWeekIsoIndex: Int,
    val timeInSeconds: Int,
    val duration: Float
)