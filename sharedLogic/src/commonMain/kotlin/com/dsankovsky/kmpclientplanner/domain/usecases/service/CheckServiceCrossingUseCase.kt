package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import kotlinx.datetime.LocalDateTime

class CheckServiceCrossingUseCase(
    private val getServicesUseCase: GetServicesUseCase
) {

    suspend fun checkCrossing(dates: List<Pair<LocalDateTime, LocalDateTime>>): List<BaseService> {
        return dates.map { dateTimePair -> checkCrossing(dateTimePair) }.flatten()
    }

    suspend fun checkCrossing(date: Pair<LocalDateTime, LocalDateTime>): List<BaseService> {
        return getServicesUseCase.getServicesInInterval(
            date.first,
            date.second
        )
    }

}