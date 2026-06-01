package com.dsankovsky.kmpclientplanner.domain.models.specific_fields

import com.dsankovsky.kmpclientplanner.data.BaseConstants

sealed interface ServiceSpecificFields {

    data class EducationServiceSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val serviceId: Long = BaseConstants.UNDEFINED_ID,
        val isOnline: Boolean = false,
        val homework: String? = null
    ) : ServiceSpecificFields

    data class BeautyServiceSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val serviceId: Long = BaseConstants.UNDEFINED_ID,
        val images: List<String> = emptyList()
    ) : ServiceSpecificFields

    data class TattooServiceSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val serviceId: Long = BaseConstants.UNDEFINED_ID,
        val images: List<String> = emptyList()
    ) : ServiceSpecificFields

    data class SportServiceSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val serviceId: Long = BaseConstants.UNDEFINED_ID,
        val isOnline: Boolean = false,
        val exercises: List<Exercise> = emptyList()
    ) : ServiceSpecificFields {

        data class Exercise(
            val title: String = "",
            val sets: List<ExerciseSet> = listOf(ExerciseSet())
        ) {
            data class ExerciseSet(
                val repeats: String = "",
                val weight: String = ""
            )
        }
    }
}