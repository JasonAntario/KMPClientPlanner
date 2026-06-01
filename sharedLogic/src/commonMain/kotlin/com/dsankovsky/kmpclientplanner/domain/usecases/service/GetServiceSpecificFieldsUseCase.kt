package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import kotlinx.coroutines.flow.firstOrNull

class GetServiceSpecificFieldsUseCase(
    private val repository: ServicesListRepository
) {

    suspend fun getSpecificField(
        serviceId: Long?,
        serviceType: ServiceType
    ): ServiceSpecificFields? {
        return when (serviceType) {
            ServiceType.EDUCATION -> {
                if (serviceId == null) return ServiceSpecificFields.EducationServiceSpecificFields()
                repository.getEducationSpecificFieldById(serviceId).firstOrNull()
                    ?: ServiceSpecificFields.EducationServiceSpecificFields()

            }

            ServiceType.BEAUTY -> {
                if (serviceId == null) return ServiceSpecificFields.BeautyServiceSpecificFields()
                repository.getBeautySpecificFieldById(serviceId).firstOrNull()
                    ?: ServiceSpecificFields.BeautyServiceSpecificFields()
            }

            ServiceType.TATTOO -> {
                if (serviceId == null) return ServiceSpecificFields.TattooServiceSpecificFields()
                repository.getTattooSpecificFieldById(serviceId).firstOrNull()
                    ?: ServiceSpecificFields.TattooServiceSpecificFields()

            }

            ServiceType.SPORT -> {
                if (serviceId == null) return ServiceSpecificFields.SportServiceSpecificFields()
                repository.getSportSpecificFieldById(serviceId).firstOrNull()
                    ?: ServiceSpecificFields.SportServiceSpecificFields()
            }

            else -> null
        }
    }
}