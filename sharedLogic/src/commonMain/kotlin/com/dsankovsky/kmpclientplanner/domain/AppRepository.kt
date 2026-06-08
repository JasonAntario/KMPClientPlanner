package com.dsankovsky.kmpclientplanner.domain

interface AppRepository {

    suspend fun clearDatabase()
}