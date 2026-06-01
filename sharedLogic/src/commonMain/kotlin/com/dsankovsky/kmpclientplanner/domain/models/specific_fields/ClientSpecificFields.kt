package com.dsankovsky.kmpclientplanner.domain.models.specific_fields

import com.dsankovsky.kmpclientplanner.data.BaseConstants
import com.dsankovsky.kmpclientplanner.extensions.getCurrentDayOfWeek
import com.dsankovsky.kmpclientplanner.extensions.getStartDateTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed interface ClientSpecificFields {

    data class EducationClientSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val clientId: Long = BaseConstants.UNDEFINED_ID,
        val level: String? = null,
        val isOnline: Boolean = false,
        val lessonDateTimeList: List<ServiceDateTime> = emptyList(),
    ) : ClientSpecificFields

    data class SportClientSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val clientId: Long = BaseConstants.UNDEFINED_ID,
        val weight: String? = null,
        val isOnline: Boolean = false,
        val lessonDateTimeList: List<ServiceDateTime> = emptyList(),
    ) : ClientSpecificFields

    data class TattooClientSpecificFields(
        val id: Long = BaseConstants.UNDEFINED_ID,
        val clientId: Long = BaseConstants.UNDEFINED_ID,
        val currentProject: TattooProject = TattooProject(),
        val finishedProjects: List<TattooProject> = emptyList()
    ) : ClientSpecificFields {

        data class TattooProject(
            val imageUrls: List<String> = emptyList()
        )
    }
}

data class ServiceDateTime(
    val dayOfWeek: DayOfWeek = getCurrentDayOfWeek(),
    val time: LocalTime = getStartDateTime().time,
    val uiTime: String = "",
    val duration: String = "1"
)