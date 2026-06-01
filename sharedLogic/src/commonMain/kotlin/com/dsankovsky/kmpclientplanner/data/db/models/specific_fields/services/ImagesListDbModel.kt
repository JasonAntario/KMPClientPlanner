package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services

import kotlinx.serialization.Serializable

@Serializable
data class ImagesListDbModel(
    val images: List<String>
)