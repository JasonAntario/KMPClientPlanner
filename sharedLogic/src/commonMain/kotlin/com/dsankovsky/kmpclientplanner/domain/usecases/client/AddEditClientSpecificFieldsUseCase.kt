package com.dsankovsky.kmpclientplanner.domain.usecases.client

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields

class AddEditClientSpecificFieldsUseCase(
    private val repository: ClientsListRepository
) {

    suspend fun addSpecificField(clientSpecificFields: ClientSpecificFields): Long {
        return when (clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields -> {
                repository.addEducationSpecificField(clientSpecificFields)
            }

            is ClientSpecificFields.TattooClientSpecificFields -> {
                repository.addTattooSpecificField(clientSpecificFields)
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                repository.addSportSpecificField(clientSpecificFields)
            }
        }
    }

    suspend fun updateSpecificField(clientSpecificFields: ClientSpecificFields) {
        when (clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields -> {
                repository.updateEducationSpecificField(clientSpecificFields)
            }

            is ClientSpecificFields.TattooClientSpecificFields -> {
                repository.updateTattooSpecificField(clientSpecificFields)
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                repository.updateSportSpecificField(clientSpecificFields)
            }
        }
    }
}