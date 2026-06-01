package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields

class AddEditServiceSpecificFieldsUseCase(
    private val repository: ServicesListRepository
) {

    suspend fun addSpecificField(serviceSpecificFields: ServiceSpecificFields): Long {
        return when (serviceSpecificFields) {
            is ServiceSpecificFields.EducationServiceSpecificFields -> {
                repository.addEducationSpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.BeautyServiceSpecificFields -> {
                repository.addBeautySpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.TattooServiceSpecificFields -> {
                repository.addTattooSpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.SportServiceSpecificFields -> {
                repository.addSportSpecificField(serviceSpecificFields)
            }
        }
    }

    suspend fun updateSpecificField(serviceSpecificFields: ServiceSpecificFields) {
        when (serviceSpecificFields) {
            is ServiceSpecificFields.EducationServiceSpecificFields -> {
                repository.updateEducationSpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.BeautyServiceSpecificFields -> {
                repository.updateBeautySpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.TattooServiceSpecificFields -> {
                repository.updateTattooSpecificField(serviceSpecificFields)
            }

            is ServiceSpecificFields.SportServiceSpecificFields -> {
                repository.updateSportSpecificField(serviceSpecificFields)
            }
        }
    }
}