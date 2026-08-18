package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * В форме услуги (М1) дата и время выбираются пикерами, а в модели живут две даты: длительность
 * — это зазор между ними. Здесь проверяется то, на чём держатся М9 и пересчёт конца занятия.
 */
class ServiceFormStateTest {

    @Test
    fun `duration comes from the gap between start and end`() {
        val state = loaded()
        assertEquals(60, state.durationMinutes)
        assertEquals(90, state.copy(endDateTime = LocalDateTime(2026, 7, 29, 16, 30)).durationMinutes)
    }

    @Test
    fun `untouched form is not dirty and every field marks it dirty`() {
        val state = loaded()
        assertFalse(state.isDirty)

        val edits = listOf(
            "название" to state.copy(title = "Математика"),
            "клиент" to state.copy(client = BaseClient(id = 2, name = "Олег")),
            "начало" to state.copy(startDateTime = LocalDateTime(2026, 7, 30, 15, 0)),
            "конец" to state.copy(endDateTime = LocalDateTime(2026, 7, 29, 17, 0)),
            "адрес" to state.copy(address = "Немига 12"),
            "комментарий" to state.copy(comment = "Перенесли"),
            "цена" to state.copy(price = "45"),
            "оплата" to state.copy(isPaid = true),
            "статус" to state.copy(isFinished = true),
            "домашнее задание" to state.copy(
                serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields(
                    homework = "Unit 5",
                ),
            ),
        )
        edits.forEach { (field, edited) ->
            assertTrue(edited.isDirty, "правка «$field» должна поднимать М9 при закрытии")
        }
    }

    @Test
    fun `duration text alone is not a change`() {
        // Пока в поле длительности стирают цифры, занятие не менялось: конец занятия
        // пересчитывается только по разобранному значению.
        assertFalse(loaded().copy(durationText = "6").isDirty)
    }

    /**
     * Вопрос «перенести остальные занятия?» задаётся, когда занятие уехало в другой слот
     * недели: серию из расписания опознают именно по дню недели и времени начала.
     */
    @Test
    fun `week slot changes only with another weekday or time`() {
        val state = loaded() // среда, 29 июля 2026, 15:00
        assertFalse(state.movedToAnotherWeekSlot, "нетронутая форма никуда не переезжала")

        val movedByWeek = state.copy(startDateTime = LocalDateTime(2026, 8, 5, 15, 0))
        assertFalse(movedByWeek.movedToAnotherWeekSlot, "та же среда, то же время — слот тот же")

        val movedToThursday = state.copy(startDateTime = LocalDateTime(2026, 7, 30, 15, 0))
        assertTrue(movedToThursday.movedToAnotherWeekSlot, "другой день недели")

        val movedToEvening = state.copy(startDateTime = LocalDateTime(2026, 7, 29, 19, 30))
        assertTrue(movedToEvening.movedToAnotherWeekSlot, "другое время")

        // У новой услуги сравнивать не с чем: серии за ней не стоит.
        assertFalse(
            state.copy(initialSnapshot = null, startDateTime = LocalDateTime(2026, 7, 30, 19, 0))
                .movedToAnotherWeekSlot,
        )
    }

    @Test
    fun `reassigning the service to another client is visible in state`() {
        val state = loaded()
        assertFalse(state.clientChanged)
        assertTrue(state.copy(client = BaseClient(id = 2, name = "Олег")).clientChanged)
    }

    private fun loaded(): AddEditServiceScreenState {
        val state = AddEditServiceScreenState(
            isLoading = false,
            isEdit = true,
            id = 1,
            title = "Английский язык",
            client = BaseClient(id = 1, name = "Анна", surname = "Ковалёва"),
            startDateTime = LocalDateTime(2026, 7, 29, 15, 0),
            endDateTime = LocalDateTime(2026, 7, 29, 16, 0),
            durationText = "60",
            address = "Онлайн",
            price = "40",
            serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields(),
        )
        return state.copy(initialSnapshot = state.snapshot)
    }
}
