package com.dsankovsky.kmpclientplanner

/**
 * Данные о сборке, которые показывает UI.
 *
 * Версия здесь своя, потому что общего для всех таргетов источника нет:
 * у Android она в `androidApp` (`versionName`), у десктопа — в `desktopApp`
 * (`packageVersion`). Эту строку видно в футере навигационного рейла,
 * при выпуске её надо обновлять вместе с обоими build-файлами.
 */
object AppInfo {
    const val VERSION: String = "1.0.0"
}
