package com.dsankovsky.kmpclientplanner.data

import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.extensions.toEpochMilliseconds
import com.dsankovsky.kmpclientplanner.data.db.AppDatabase
import com.dsankovsky.kmpclientplanner.data.db.dao.service.BeautyServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.EducationServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.ServicesDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.SportServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.TattooServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.mappers.base.toBaseService
import com.dsankovsky.kmpclientplanner.data.mappers.base.toBaseServicesList
import com.dsankovsky.kmpclientplanner.data.mappers.base.toDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toBeautyServiceSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toBeautyServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toEducationServiceSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toEducationServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toSportServiceSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toSportServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toTattooServiceSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service.toTattooServiceSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class ServicesListRepositoryImpl(
    database: AppDatabase
) : ServicesListRepository {

    private val servicesDao: ServicesDao = database.servicesDao()
    private val beautyServiceFieldsDao: BeautyServiceFieldsDao = database.beautyServiceFieldsDao()
    private val educationServiceFieldsDao: EducationServiceFieldsDao =
        database.educationServiceFieldsDao()
    private val sportServiceFieldsDao: SportServiceFieldsDao = database.sportServiceFieldsDao()
    private val tattooServiceFieldsDao: TattooServiceFieldsDao = database.tattooServiceFieldsDao()

    override fun getAllServices(): Flow<List<BaseService>> {
        return servicesDao.getAllServices().map { it.toBaseServicesList() }
    }

    override fun getAllServicesForHomeScreen(): Flow<List<BaseService>> {
        return servicesDao.getAllServicesExceptPaidAndFinished().map { it.toBaseServicesList() }
    }

    override fun getServiceByServiceId(serviceId: Long): Flow<BaseService> {
        return servicesDao.getServiceById(serviceId).map { it.toBaseService() }
    }

    override suspend fun getServiceByClientId(clientId: Long): List<BaseService> {
        return servicesDao.getServiceByClientId(clientId).map { it.toBaseService() }
    }

    override fun getServiceByClientIdFlow(clientId: Long): Flow<List<BaseService>> {
        return servicesDao.getServiceByClientIdFlow(clientId).map { it.toBaseServicesList() }
    }

    override suspend fun getLastServiceByClientId(clientId: Long): BaseService {
        return servicesDao.getLastServiceByClientId(clientId).toBaseService()
    }

    override suspend fun addService(service: BaseService): Long {
        return servicesDao.addService(service.toDbModel())
    }

    override suspend fun updateService(service: BaseService) {
        servicesDao.updateService(service.toDbModel())
    }

    override suspend fun deleteService(service: BaseService) {
        servicesDao.deleteService(service.toDbModel())
    }

    override suspend fun deleteService(serviceId: Long) {
        servicesDao.deleteService(serviceId)
    }

    override fun getAllUnpaidServices(clientId: Long): Flow<List<BaseService>> {
        return servicesDao.getAllUnpaidServices(clientId).map { it.toBaseServicesList() }
    }

    override suspend fun getServicesInInterval(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<BaseService> {
        return servicesDao.getServicesInInterval(
            startDateTime.toEpochMilliseconds(),
            endDateTime.toEpochMilliseconds()
        ).map { it.toBaseService() }
    }

    override fun getEducationSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.EducationServiceSpecificFields> {
        return educationServiceFieldsDao.getEducationServiceSpecificFieldByServiceId(serviceId)
            .map { it.toEducationServiceSpecificFields() }
    }

    override suspend fun addEducationSpecificField(field: ServiceSpecificFields.EducationServiceSpecificFields): Long {
        return educationServiceFieldsDao.addEducationServiceSpecificField(field.toEducationServiceSpecificFieldsDbModel())
    }

    override suspend fun updateEducationSpecificField(field: ServiceSpecificFields.EducationServiceSpecificFields) {
        educationServiceFieldsDao.updateEducationServiceSpecificField(field.toEducationServiceSpecificFieldsDbModel())
    }

    override fun getBeautySpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.BeautyServiceSpecificFields> {
        return beautyServiceFieldsDao.getBeautyServiceSpecificFieldByServiceId(serviceId)
            .map { it.toBeautyServiceSpecificFields() }
    }

    override suspend fun addBeautySpecificField(field: ServiceSpecificFields.BeautyServiceSpecificFields): Long {
        return beautyServiceFieldsDao.addBeautyServiceSpecificField(field.toBeautyServiceSpecificFieldsDbModel())
    }

    override suspend fun updateBeautySpecificField(field: ServiceSpecificFields.BeautyServiceSpecificFields) {
        beautyServiceFieldsDao.updateBeautyServiceSpecificField(field.toBeautyServiceSpecificFieldsDbModel())
    }

    override fun getTattooSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.TattooServiceSpecificFields> {
        return tattooServiceFieldsDao.getTattooServiceSpecificFieldByServiceId(serviceId)
            .map { it.toTattooServiceSpecificFields() }
    }

    override suspend fun addTattooSpecificField(field: ServiceSpecificFields.TattooServiceSpecificFields): Long {
        return tattooServiceFieldsDao.addTattooServiceSpecificField(field.toTattooServiceSpecificFieldsDbModel())
    }

    override suspend fun updateTattooSpecificField(field: ServiceSpecificFields.TattooServiceSpecificFields) {
        tattooServiceFieldsDao.updateTattooServiceSpecificField(field.toTattooServiceSpecificFieldsDbModel())
    }

    override fun getSportSpecificFieldById(serviceId: Long): Flow<ServiceSpecificFields.SportServiceSpecificFields> {
        return sportServiceFieldsDao.getSportServiceSpecificFieldByServiceId(serviceId)
            .map { it.toSportServiceSpecificFields() }
    }

    override suspend fun addSportSpecificField(field: ServiceSpecificFields.SportServiceSpecificFields): Long {
        return sportServiceFieldsDao.addSportServiceSpecificField(field.toSportServiceSpecificFieldsDbModel())
    }

    override suspend fun updateSportSpecificField(field: ServiceSpecificFields.SportServiceSpecificFields) {
        sportServiceFieldsDao.updateSportServiceSpecificField(field.toSportServiceSpecificFieldsDbModel())
    }
}