package com.dsankovsky.kmpclientplanner.domain.usecases.service

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.extensions.addHours
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeClientsRepository
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Перенос серии занятий вслед за одним отредактированным.
 *
 * Занятия ставятся расписанием, поэтому «остальные занятия» — это те, что стоят в том же
 * слоте недели, каким занятие было **до** правки: в базе они ещё по старому дню и времени.
 */
class ShiftFutureServicesUseCaseTest {

    /** Слот серии до правки: среда, 15 июля 2026, 15:00. */
    private val oldStart = LocalDateTime(2026, 7, 15, 15, 0)
    private val now = LocalDateTime(2026, 7, 15, 12, 0)

    @Test
    fun `finds only future services of the same week slot`() = runTest {
        val services = MutableStateFlow(
            listOf(
                // Само отредактированное занятие: его переносить не надо, оно уже сохранено.
                service(id = 1, start = oldStart),
                // Серия: следующие среды в 15:00.
                service(id = 2, start = LocalDateTime(2026, 7, 22, 15, 0)),
                service(id = 3, start = LocalDateTime(2026, 7, 29, 15, 0)),
                // Прошедшая среда — уже проведена, не наше дело.
                service(id = 4, start = LocalDateTime(2026, 7, 8, 15, 0)),
                // Среда, но другое время — другой слот.
                service(id = 5, start = LocalDateTime(2026, 7, 22, 19, 0)),
                // То же время, но пятница — другой слот.
                service(id = 6, start = LocalDateTime(2026, 7, 24, 15, 0)),
                // Другой клиент.
                service(id = 7, start = LocalDateTime(2026, 7, 22, 15, 0), clientId = 2),
            ),
        )

        val found = useCase(services).findServicesToShift(
            clientId = ClientId,
            serviceId = 1,
            oldStart = oldStart,
            now = now,
        )

        assertEquals(listOf(2L, 3L), found.map { it.id })
    }

    @Test
    fun `shifts each service inside its own week and keeps its duration`() = runTest {
        val services = MutableStateFlow(
            listOf(
                service(id = 2, start = LocalDateTime(2026, 7, 22, 15, 0)),
                // У этого занятия своя длительность — перенос её не трогает.
                service(
                    id = 3,
                    start = LocalDateTime(2026, 7, 29, 15, 0),
                    end = LocalDateTime(2026, 7, 29, 16, 30),
                ),
            ),
        )

        // Занятие переехало со среды 15:00 на пятницу 19:30.
        val result = useCase(services).shift(
            services = services.value,
            oldStart = oldStart,
            newStart = LocalDateTime(2026, 7, 17, 19, 30),
            clientId = ClientId,
            serviceType = ServiceType.BASE,
        )

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow())

        val shifted = services.value.associateBy { it.id }
        assertEquals(LocalDateTime(2026, 7, 24, 19, 30), shifted.getValue(2L).startDate)
        assertEquals(LocalDateTime(2026, 7, 24, 20, 30), shifted.getValue(2L).endDate)
        assertEquals(LocalDateTime(2026, 7, 31, 19, 30), shifted.getValue(3L).startDate)
        assertEquals(
            LocalDateTime(2026, 7, 31, 21, 0),
            shifted.getValue(3L).endDate,
            "полтора часа остаются полутора часами",
        )
    }

    @Test
    fun `shifts backwards when the new weekday is earlier`() = runTest {
        val services = MutableStateFlow(
            listOf(service(id = 2, start = LocalDateTime(2026, 7, 22, 15, 0))),
        )

        // Со среды на понедельник — занятие остаётся в своей неделе, просто раньше.
        useCase(services).shift(
            services = services.value,
            oldStart = oldStart,
            newStart = LocalDateTime(2026, 7, 13, 11, 0),
            clientId = ClientId,
            serviceType = ServiceType.BASE,
        )

        assertEquals(LocalDateTime(2026, 7, 20, 11, 0), services.value.single().startDate)
    }

    /**
     * Пример из задачи: занятия по вторникам в 11:00 переезжают на среду 13:00 — вместе с
     * ними на новый слот встаёт и строка расписания в карточке, а четверг остаётся как был.
     */
    @Test
    fun `shifts the matching schedule slot in the client card`() = runTest {
        // Вторник, 14 июля 2026, 11:00 — слот серии.
        val tuesday11 = LocalDateTime(2026, 7, 14, 11, 0)
        val services = MutableStateFlow(
            listOf(service(id = 2, start = LocalDateTime(2026, 7, 21, 11, 0))),
        )
        val schedule = schedule()
        val useCase = useCase(services, schedule)

        assertTrue(
            useCase.hasScheduleSlot(ClientId, ServiceType.EDUCATION, tuesday11),
            "строка вторник 11:00 в расписании есть",
        )

        // Переносим на среду 13:00.
        val result = useCase.shift(
            services = services.value,
            oldStart = tuesday11,
            newStart = LocalDateTime(2026, 7, 15, 13, 0),
            clientId = ClientId,
            serviceType = ServiceType.EDUCATION,
        )

        assertTrue(result.isSuccess)
        assertEquals(LocalDateTime(2026, 7, 22, 13, 0), services.value.single().startDate)

        val slots = schedule.value?.lessonDateTimeList.orEmpty()
        assertEquals(DayOfWeek.WEDNESDAY, slots[0].dayOfWeek)
        assertEquals(LocalTime(13, 0), slots[0].time)
        assertEquals("1", slots[0].duration, "длительность строки не наше дело")
        assertEquals(
            ServiceDateTime(DayOfWeek.THURSDAY, LocalTime(22, 0), duration = "1.5"),
            slots[1],
            "четверг переезжать не должен",
        )
        assertEquals(7L, schedule.value?.id, "обновляется та же строка, а не создаётся новая")
    }

    @Test
    fun `leaves the schedule alone when no slot matches`() = runTest {
        val schedule = schedule()
        val before = schedule.value
        // Занятие стояло в среду — такой строки в расписании нет.
        val wednesday = LocalDateTime(2026, 7, 15, 9, 0)
        val useCase = useCase(MutableStateFlow(emptyList()), schedule)

        assertFalse(useCase.hasScheduleSlot(ClientId, ServiceType.EDUCATION, wednesday))

        useCase.shift(
            services = emptyList(),
            oldStart = wednesday,
            newStart = LocalDateTime(2026, 7, 16, 9, 0),
            clientId = ClientId,
            serviceType = ServiceType.EDUCATION,
        )

        assertEquals(before, schedule.value)
    }

    private fun useCase(
        services: MutableStateFlow<List<BaseService>>,
        schedule: MutableStateFlow<ClientSpecificFields.EducationClientSpecificFields?> =
            MutableStateFlow(null),
    ): ShiftFutureServicesUseCase {
        val servicesRepository = FakeServicesRepository(services)
        val clientsRepository = FakeClientsRepository(
            clients = MutableStateFlow(listOf(BaseClient(id = ClientId))),
            educationFields = schedule,
        )
        return ShiftFutureServicesUseCase(
            getServicesUseCase = GetServicesUseCase(servicesRepository),
            addEditDeleteServiceUseCase = AddEditDeleteServiceUseCase(servicesRepository),
            getClientSpecificFieldsUseCase = GetClientSpecificFieldsUseCase(clientsRepository),
            addEditClientSpecificFieldsUseCase =
                AddEditClientSpecificFieldsUseCase(clientsRepository),
        )
    }

    /** Расписание клиента из примера: вторник 11:00 на час и четверг 22:00 на полтора. */
    private fun schedule() = MutableStateFlow<ClientSpecificFields.EducationClientSpecificFields?>(
        ClientSpecificFields.EducationClientSpecificFields(
            id = 7,
            clientId = ClientId,
            lessonDateTimeList = listOf(
                ServiceDateTime(DayOfWeek.TUESDAY, LocalTime(11, 0), duration = "1"),
                ServiceDateTime(DayOfWeek.THURSDAY, LocalTime(22, 0), duration = "1.5"),
            ),
        ),
    )

    private fun service(
        id: Long,
        start: LocalDateTime,
        end: LocalDateTime = start.addHours(1),
        clientId: Long = ClientId,
    ) = BaseService(id = id, clientId = clientId, startDate = start, endDate = end)

    private companion object {
        const val ClientId = 1L
    }
}
