package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService

class AddEditDeleteServiceUseCase(
    private val repository: ServicesListRepository
) {

    suspend fun addService(service: BaseService): Long {
        return repository.addService(service)
    }

    suspend fun update(service: BaseService) {
        repository.updateService(service)
    }

    suspend fun deleteService(serviceId: Long) {
        repository.deleteService(serviceId)
    }
}