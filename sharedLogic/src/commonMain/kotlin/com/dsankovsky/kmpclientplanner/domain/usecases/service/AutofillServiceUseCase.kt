package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.extensions.calculateNextDateTime
import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

class AutofillServiceUseCase(
    private val getClientsUseCase: GetClientsUseCase,
    private val checkServiceCrossingUseCase: CheckServiceCrossingUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
    private val addEditServiceSpecificFieldsUseCase: AddEditServiceSpecificFieldsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase
) {

    fun askAboutAutofill(
        specificFields: ClientSpecificFields?,
        initialSpecificFields: ClientSpecificFields?,
        serviceType: ServiceType
    ): Boolean {
        return when (serviceType) {
            ServiceType.BASE, ServiceType.TATTOO, ServiceType.BEAUTY -> false
            ServiceType.EDUCATION -> {
                if (specificFields == initialSpecificFields)
                    false
                else {
                    val clientSpecificFields =
                        specificFields as? ClientSpecificFields.EducationClientSpecificFields
                    val serviceTimeList = clientSpecificFields?.lessonDateTimeList
                    !serviceTimeList.isNullOrEmpty()
                }
            }

            ServiceType.SPORT -> {
                if (specificFields == initialSpecificFields)
                    false
                else {
                    val clientSpecificFields =
                        specificFields as? ClientSpecificFields.SportClientSpecificFields
                    val serviceTimeList = clientSpecificFields?.lessonDateTimeList
                    !serviceTimeList.isNullOrEmpty()
                }
            }
        }
    }

    fun askAboutAutofill(
        specificFields: ClientSpecificFields?,
        serviceType: ServiceType
    ): Boolean {
        return when (serviceType) {
            ServiceType.BASE, ServiceType.TATTOO, ServiceType.BEAUTY -> false
            ServiceType.EDUCATION -> {
                val clientSpecificFields =
                    specificFields as? ClientSpecificFields.EducationClientSpecificFields
                val serviceTimeList = clientSpecificFields?.lessonDateTimeList
                !serviceTimeList.isNullOrEmpty()
            }

            ServiceType.SPORT -> {
                val clientSpecificFields =
                    specificFields as? ClientSpecificFields.SportClientSpecificFields
                val serviceTimeList = clientSpecificFields?.lessonDateTimeList
                !serviceTimeList.isNullOrEmpty()
            }
        }
    }

    suspend fun autofillServices(
        clientId: Long,
        serviceType: ServiceType,
        ignoreCrossing: Boolean = false,
        startDateTime: LocalDateTime = getCurrentDateTime(),
        weeks: Int = 4,
        titlePrefix: String = ""
    ): Result<ServicesAutofillResult> {
        val client = getClientsUseCase.getClientById(clientId)
            .firstOrNull() ?: return Result.failure(ServicesAutofillResultError.NoClient())

        when (serviceType) {
            ServiceType.BASE, ServiceType.BEAUTY, ServiceType.TATTOO -> return Result.success(
                ServicesAutofillResult.NoAutofillNeeded
            )

            ServiceType.EDUCATION -> {
                val clientSpecificFields = getClientSpecificFieldsUseCase.getSpecificField(
                    clientId,
                    serviceType
                ) as? ClientSpecificFields.EducationClientSpecificFields
                    ?: return Result.failure(
                        ServicesAutofillResultError.NoClientSpecificFields()
                    )

                val dates = clientSpecificFields.lessonDateTimeList.calculateNextDateTime(
                    startDateTime = startDateTime,
                    repeat = weeks
                )
                if (ignoreCrossing.not()) {
                    val crossingServices = checkServiceCrossingUseCase.checkCrossing(dates)

                    if (crossingServices.isNotEmpty()) {
                        return Result.failure(
                            ServicesAutofillResultError.ServicesCrossing(
                                crossingServices
                            )
                        )
                    }
                }

                dates.forEach { dateTimePair ->
                    val service = BaseService(
                        title = "$titlePrefix ${client.name}",
                        clientId = clientId,
                        startDate = dateTimePair.first,
                        endDate = dateTimePair.second,
                        price = client.price,
                        currency = client.currency,
                        serviceType = client.serviceType
                    )
                    val serviceId = addEditDeleteServiceUseCase.addService(service)
                    val specificFields =
                        ServiceSpecificFields.EducationServiceSpecificFields(serviceId = serviceId)
                    addEditServiceSpecificFieldsUseCase.addSpecificField(specificFields)
                }
                return Result.success(ServicesAutofillResult.AutofillCompleted)
            }

            ServiceType.SPORT -> {
                val clientSpecificFields = getClientSpecificFieldsUseCase.getSpecificField(
                    clientId,
                    serviceType
                ) as? ClientSpecificFields.SportClientSpecificFields ?: return Result.failure(
                    ServicesAutofillResultError.NoClientSpecificFields()
                )

                val dates = clientSpecificFields.lessonDateTimeList.calculateNextDateTime(
                    startDateTime = startDateTime,
                    repeat = weeks
                )
                if (ignoreCrossing.not()) {
                    val crossingServices = checkServiceCrossingUseCase.checkCrossing(dates)

                    if (crossingServices.isNotEmpty()) {
                        return Result.failure(
                            ServicesAutofillResultError.ServicesCrossing(
                                crossingServices
                            )
                        )
                    }
                }

                dates.forEach { dateTimePair ->
                    val service = BaseService(
                        title = "$titlePrefix ${client.name}",
                        clientId = clientId,
                        startDate = dateTimePair.first,
                        endDate = dateTimePair.second,
                        price = client.price,
                        currency = client.currency,
                        serviceType = client.serviceType
                    )
                    val serviceId = addEditDeleteServiceUseCase.addService(service)
                    val specificFields =
                        ServiceSpecificFields.SportServiceSpecificFields(serviceId = serviceId)
                    addEditServiceSpecificFieldsUseCase.addSpecificField(specificFields)
                }

                return Result.success(ServicesAutofillResult.AutofillCompleted)
            }
        }
    }
}

sealed interface ServicesAutofillResult {
    data object NoAutofillNeeded : ServicesAutofillResult
    data object AutofillCompleted : ServicesAutofillResult
}

sealed class ServicesAutofillResultError : Throwable() {
    class NoClient : ServicesAutofillResultError()
    class NoClientSpecificFields : ServicesAutofillResultError()
    data class ServicesCrossing(val services: List<BaseService>) :
        ServicesAutofillResultError()
}