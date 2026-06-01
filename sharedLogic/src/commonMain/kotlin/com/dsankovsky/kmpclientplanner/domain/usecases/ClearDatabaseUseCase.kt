package com.dsankovsky.kmpclientplanner.domain.usecases

import com.dsankovsky.kmpclientplanner.domain.AppRepository

class ClearDatabaseUseCase(
    private val appRepository: AppRepository
) {

    fun clearDatabase() {
        appRepository.clearDatabase()
    }
}