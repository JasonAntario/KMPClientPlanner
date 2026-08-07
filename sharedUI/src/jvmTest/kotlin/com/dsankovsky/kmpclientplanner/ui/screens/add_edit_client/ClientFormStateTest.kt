package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * М9 («Закрыть без сохранения?») спрашивают только когда в форме правда что-то поменяли.
 * Признак живёт в состоянии — сравнением со слепком на момент открытия, поэтому и проверяется
 * здесь, без поднятия вью-модели с семью зависимостями.
 */
class ClientFormStateTest {

    @Test
    fun `untouched form is not dirty`() {
        val state = loaded()
        assertFalse(state.isDirty, "форму открыли и не тронули")
        assertTrue(state.canSave)
    }

    @Test
    fun `every editable field marks the form dirty`() {
        val state = loaded()
        val edits = listOf(
            "имя" to state.copy(name = "Анна Мария"),
            "фамилия" to state.copy(surname = "Ковалевич"),
            "телефон" to state.copy(phone = "+375 29 000-00-00"),
            "адрес" to state.copy(address = "Немига 12"),
            "цена" to state.copy(price = "45"),
            "валюта" to state.copy(currency = CurrencyItem.EUR),
            "комментарий" to state.copy(comment = "Другой"),
            "уровень" to state.copy(
                clientSpecificFields = educationFields().copy(level = "B2"),
            ),
            "расписание" to state.copy(
                clientSpecificFields = educationFields().copy(
                    lessonDateTimeList = educationFields().lessonDateTimeList + ServiceDateTime(),
                ),
            ),
        )

        edits.forEach { (field, edited) ->
            assertTrue(edited.isDirty, "правка «$field» должна поднимать М9 при закрытии")
        }
    }

    @Test
    fun `returning the value back clears the dirty flag`() {
        val state = loaded()
        val edited = state.copy(name = "Анна Мария")
        assertTrue(edited.isDirty)
        assertFalse(edited.copy(name = state.name).isDirty, "значение вернули — спрашивать нечего")
    }

    @Test
    fun `client without a name cannot be saved`() {
        assertFalse(loaded().copy(name = "  ").canSave)
    }

    /** Форма после загрузки клиента: слепок совпадает с полями. */
    private fun loaded(): AddEditClientScreenState {
        val state = AddEditClientScreenState(
            isLoading = false,
            isEdit = true,
            id = 1,
            name = "Анна",
            surname = "Ковалёва",
            phone = "+375 29 123-45-67",
            address = "Онлайн",
            price = "40",
            comment = "Готовится к экзамену",
            clientSpecificFields = educationFields(),
        )
        return state.copy(initialSnapshot = state.snapshot)
    }

    private fun educationFields() = ClientSpecificFields.EducationClientSpecificFields(
        level = "B1",
        lessonDateTimeList = listOf(ServiceDateTime()),
    )
}
