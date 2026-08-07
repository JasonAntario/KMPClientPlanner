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
