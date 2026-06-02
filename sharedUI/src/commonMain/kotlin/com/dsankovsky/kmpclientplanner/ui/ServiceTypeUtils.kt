package com.dsankovsky.kmpclientplanner.ui

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType

fun ServiceType.displayName(): String = when (this) {
    ServiceType.EDUCATION -> "Репетитор"
    ServiceType.SPORT -> "Тренер"
    ServiceType.BEAUTY -> "Бьюти-мастер"
    ServiceType.TATTOO -> "Тату-мастер"
    ServiceType.BASE -> "Другая услуга"
}

fun ServiceType.description(): String = when (this) {
    ServiceType.EDUCATION -> "Домашние задания, уровень знаний"
    ServiceType.SPORT -> "Программы тренировок, упражнения"
    ServiceType.BEAUTY -> "Референсы и результаты работ"
    ServiceType.TATTOO -> "Эскизы, текущие проекты"
    ServiceType.BASE -> "Базовый набор функций"
}

fun ServiceType.emoji(): String = when (this) {
    ServiceType.EDUCATION -> "✏️"
    ServiceType.SPORT -> "💪"
    ServiceType.BEAUTY -> "💅"
    ServiceType.TATTOO -> "🎨"
    ServiceType.BASE -> "⚙️"
}
