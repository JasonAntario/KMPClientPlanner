package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResult
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResultError
import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import kotlinx.datetime.LocalDateTime

/**
 * Swift-friendly outcome for autofill.
 *
 * `AutofillServiceUseCase.autofillServices` returns `kotlin.Result`, which Kotlin/Native
 * does not export to Objective-C/Swift. This bridge folds the Result into a sealed class
 * (which Skie exposes as a Swift enum). No business logic lives here — it only adapts the
 * return shape.
 */
sealed class IosAutofillOutcome {
    data object Completed : IosAutofillOutcome()
    data object NoAutofillNeeded : IosAutofillOutcome()
    data object NoClient : IosAutofillOutcome()
    data object NoClientSpecificFields : IosAutofillOutcome()
    data class Crossing(val services: List<BaseService>) : IosAutofillOutcome()
}

suspend fun AutofillServiceUseCase.autofillServicesIos(
    clientId: Long,
    serviceType: ServiceType,
    ignoreCrossing: Boolean,
    titlePrefix: String,
    startDateTime: LocalDateTime = getCurrentDateTime()
): IosAutofillOutcome {
    return autofillServices(
        clientId = clientId,
        serviceType = serviceType,
        ignoreCrossing = ignoreCrossing,
        startDateTime = startDateTime,
        titlePrefix = titlePrefix
    ).fold(
        onSuccess = {
            when (it) {
                ServicesAutofillResult.AutofillCompleted -> IosAutofillOutcome.Completed
                ServicesAutofillResult.NoAutofillNeeded -> IosAutofillOutcome.NoAutofillNeeded
            }
        },
        onFailure = {
            when (val error = it as? ServicesAutofillResultError) {
                is ServicesAutofillResultError.NoClient -> IosAutofillOutcome.NoClient
                is ServicesAutofillResultError.NoClientSpecificFields -> IosAutofillOutcome.NoClientSpecificFields
                is ServicesAutofillResultError.ServicesCrossing -> IosAutofillOutcome.Crossing(error.services)
                null -> IosAutofillOutcome.NoClient
            }
        }
    )
}
