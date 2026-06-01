package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getDateInterval
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class GetServicesUseCase(
    private val repository: ServicesListRepository
) {
    fun getAllServices(
        filter: ServicesFilter
    ): Flow<List<BaseService>> {
        return repository.getAllServices().map {
            it.filter { service ->
                val dateInterval = filter.getDateInterval() ?: return@filter true
                when (filter) {
                    ServicesFilter.CURRENT_MONTH -> {
                        service.startDate.month == dateInterval.first.month
                    }

                    ServicesFilter.YEAR -> {
                        service.startDate.year == dateInterval.first.year
                    }

                    else -> service.startDate.date in dateInterval.first..dateInterval.second
                }
            }
        }
    }

    fun getAllServices(): Flow<List<BaseService>> {
        return repository.getAllServices()
    }

    suspend fun getServicesForClient(
        clientId: Long
    ): List<BaseService> {
        return repository.getServiceByClientId(clientId)
    }

    fun getServicesForClientFlow(
        clientId: Long
    ): Flow<List<BaseService>> {
        return repository.getServiceByClientIdFlow(clientId)
    }

    suspend fun getLastServiceForClient(
        clientId: Long
    ): BaseService {
        return repository.getLastServiceByClientId(clientId)
    }

    fun getAllUnpaidServices(clientId: Long): Flow<List<BaseService>> {
        return repository.getAllUnpaidServices(clientId)
    }

    suspend fun getAddressesList(): List<String> {
        return repository.getAllServices()
            .firstOrNull()
            ?.mapNotNull { it.address }
            ?.distinct() ?: emptyList()
    }

    suspend fun getServicesInInterval(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<BaseService> {
        return repository.getServicesInInterval(startDateTime, endDateTime)
    }

    fun getServiceById(serviceId: Long?): Flow<BaseService?> {
        if (serviceId == null) return flowOf(null)
        return repository.getServiceByServiceId(serviceId)
    }
}