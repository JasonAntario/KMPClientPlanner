package com.dsankovsky.kmpclientplanner.domain

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface ServicesListRepository {

    fun getAllServices(): Flow<List<BaseService>>
    fun getAllServicesForHomeScreen(): Flow<List<BaseService>>
    fun getServiceByServiceId(serviceId: Long): Flow<BaseService>
    suspend fun getServiceByClientId(clientId: Long): List<BaseService>
    fun getServiceByClientIdFlow(clientId: Long): Flow<List<BaseService>>
    suspend fun getLastServiceByClientId(clientId: Long): BaseService
    suspend fun addService(service: BaseService): Long
    suspend fun updateService(service: BaseService)
    suspend fun deleteService(service: BaseService)
    suspend fun deleteService(serviceId: Long)

    fun getAllUnpaidServices(clientId: Long): Flow<List<BaseService>>

    suspend fun getServicesInInterval(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<BaseService>

    fun getEducationSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.EducationServiceSpecificFields>
    suspend fun addEducationSpecificField(field: ServiceSpecificFields.EducationServiceSpecificFields): Long
    suspend fun updateEducationSpecificField(field: ServiceSpecificFields.EducationServiceSpecificFields)

    fun getBeautySpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.BeautyServiceSpecificFields>
    suspend fun addBeautySpecificField(field: ServiceSpecificFields.BeautyServiceSpecificFields): Long
    suspend fun updateBeautySpecificField(field: ServiceSpecificFields.BeautyServiceSpecificFields)

    fun getTattooSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.TattooServiceSpecificFields>
    suspend fun addTattooSpecificField(field: ServiceSpecificFields.TattooServiceSpecificFields): Long
    suspend fun updateTattooSpecificField(field: ServiceSpecificFields.TattooServiceSpecificFields)

    fun getSportSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.SportServiceSpecificFields>
    suspend fun addSportSpecificField(field: ServiceSpecificFields.SportServiceSpecificFields): Long
    suspend fun updateSportSpecificField(field: ServiceSpecificFields.SportServiceSpecificFields)
}