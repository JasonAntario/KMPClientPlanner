package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.extensions.addHours
import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.extensions.toEpochMilliseconds
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

/**
 * Перенос регулярных занятий клиента вслед за одним отредактированным.
 *
 * Занятия создаются расписанием, поэтому у клиента их обычно целая серия в одном слоте
 * недели. Когда одно занятие переезжает на другой день или время, вместе с ним переезжает
 * и вся серия, и сама строка расписания в карточке клиента — иначе следующее
 * автозаполнение вернёт занятия в старый слот.
 *
 * «Тот же слот» считается по занятию **до** правки: и будущие занятия в базе, и расписание
 * в карточке стоят ещё по старому дню недели и старому времени.
 */
class ShiftFutureServicesUseCase(
    private val getServicesUseCase: GetServicesUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val addEditClientSpecificFieldsUseCase: AddEditClientSpecificFieldsUseCase,
) {

    /**
     * Будущие занятия клиента из того же слота недели, кроме самого отредактированного.
     *
     * @param oldStart начало занятия до правки — по нему и опознаётся слот
     */
    suspend fun findServicesToShift(
        clientId: Long,
        serviceId: Long,
        oldStart: LocalDateTime,
        now: LocalDateTime = getCurrentDateTime(),
    ): List<BaseService> {
        return getServicesUseCase.getServicesForClient(clientId)
            .filter { service ->
                service.id != serviceId &&
                    service.startDate > now &&
                    service.startDate.time == oldStart.time &&
                    service.startDate.dayOfWeek == oldStart.dayOfWeek
            }
            .sortedBy { it.startDate }
    }

    /** Стоит ли в расписании клиента строка того же слота — её тоже надо будет перенести. */
    suspend fun hasScheduleSlot(
        clientId: Long,
        serviceType: ServiceType,
        oldStart: LocalDateTime,
    ): Boolean {
        return scheduleOf(clientId, serviceType)?.any { it.isInSlot(oldStart) } == true
    }

    /**
     * Сдвигает занятия в новый слот недели, каждое — внутри своей недели: меняется день
     * недели и время начала, длительность у каждого остаётся своя. Заодно на новый слот
     * переезжает строка расписания в карточке клиента, если она там есть.
     *
     * @return сколько занятий перенесено
     */
    suspend fun shift(
        services: List<BaseService>,
        oldStart: LocalDateTime,
        newStart: LocalDateTime,
        clientId: Long,
        serviceType: ServiceType,
    ): Result<Int> = runCatching {
        val daysShift = newStart.dayOfWeek.isoDayNumber - oldStart.dayOfWeek.isoDayNumber
        services.forEach { service ->
            val start = LocalDateTime(
                date = service.startDate.date.plus(daysShift, DateTimeUnit.DAY),
                time = newStart.time,
            )
            addEditDeleteServiceUseCase.update(
                service.copy(startDate = start, endDate = start.addHours(service.durationHours)),
            )
        }
        shiftSchedule(clientId, serviceType, oldStart, newStart)
        services.size
    }

    /**
     * Переносит строку расписания в карточке клиента. Остальные строки — другие дни недели —
     * остаются как есть, длительность у перенесённой своя: переезжает только слот.
     */
    private suspend fun shiftSchedule(
        clientId: Long,
        serviceType: ServiceType,
        oldStart: LocalDateTime,
        newStart: LocalDateTime,
    ) {
        val fields = getClientSpecificFieldsUseCase.getSpecificField(clientId, serviceType)
        val schedule = fields.scheduleOrNull() ?: return
        if (schedule.none { it.isInSlot(oldStart) }) return

        val shifted = schedule.map { slot ->
            if (slot.isInSlot(oldStart)) {
                slot.copy(dayOfWeek = newStart.dayOfWeek, time = newStart.time)
            } else {
                slot
            }
        }

        val updated = when (fields) {
            is ClientSpecificFields.EducationClientSpecificFields ->
                fields.copy(lessonDateTimeList = shifted)

            is ClientSpecificFields.SportClientSpecificFields ->
                fields.copy(lessonDateTimeList = shifted)

            // У тату расписания нет, здесь это уже отфильтровано `scheduleOrNull`.
            else -> return
        }
        addEditClientSpecificFieldsUseCase.updateSpecificField(updated)
    }

    private suspend fun scheduleOf(clientId: Long, serviceType: ServiceType): List<ServiceDateTime>? =
        getClientSpecificFieldsUseCase.getSpecificField(clientId, serviceType).scheduleOrNull()
}

/** Расписание есть только у категорий с регулярными занятиями. */
private fun ClientSpecificFields?.scheduleOrNull(): List<ServiceDateTime>? = when (this) {
    is ClientSpecificFields.EducationClientSpecificFields -> lessonDateTimeList
    is ClientSpecificFields.SportClientSpecificFields -> lessonDateTimeList
    else -> null
}

/** Строка расписания описывает тот же слот недели, что и занятие до правки. */
private fun ServiceDateTime.isInSlot(start: LocalDateTime): Boolean =
    dayOfWeek == start.dayOfWeek && time == start.time

/** Длительность занятия в часах: в модели живут две даты, а сдвигается только начало. */
private val BaseService.durationHours: Float
    get() {
        val millis = endDate.toEpochMilliseconds() - startDate.toEpochMilliseconds()
        return millis / MillisInHour
    }

private const val MillisInHour = 60f * 60f * 1000f
