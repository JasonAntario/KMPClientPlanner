package com.dsankovsky.kmpclientplanner.domain.usecases.client

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import kotlinx.coroutines.flow.firstOrNull

class GetClientSpecificFieldsUseCase(
    private val clientsRepository: ClientsListRepository
) {

    suspend fun getSpecificField(
        clientId: Long?,
        serviceType: ServiceType
    ): ClientSpecificFields? {
        return when (serviceType) {
            ServiceType.EDUCATION -> {
                if (clientId == null) return ClientSpecificFields.EducationClientSpecificFields()
                clientsRepository.getEducationSpecificFieldByClientId(clientId)
                    .firstOrNull() ?: ClientSpecificFields.EducationClientSpecificFields()

            }

            ServiceType.TATTOO -> {
                if (clientId == null) return ClientSpecificFields.TattooClientSpecificFields()
                clientsRepository.getTattooSpecificFieldByClientId(clientId).firstOrNull()
                    ?: ClientSpecificFields.TattooClientSpecificFields()
            }

            ServiceType.SPORT -> {
                if (clientId == null) return ClientSpecificFields.SportClientSpecificFields()
                clientsRepository.getSportSpecificFieldByClientId(clientId).firstOrNull()
                    ?: ClientSpecificFields.SportClientSpecificFields()
            }

            else -> null
        }
    }
}