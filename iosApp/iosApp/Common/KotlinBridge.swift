import Foundation
import SharedLogic

// kotlinx-datetime types are exported with a module prefix; alias them for readability.
typealias LocalDate = Kotlinx_datetimeLocalDate
typealias LocalDateTime = Kotlinx_datetimeLocalDateTime
typealias LocalTime = Kotlinx_datetimeLocalTime
typealias DayOfWeek = Kotlinx_datetimeDayOfWeek

// MARK: - Kotlin bridging helpers
//
// This file centralizes every place where Swift touches a Skie/Kotlin-Native API
// whose generated symbol name can only be confirmed against a real framework build
// (kotlinx-datetime types, Flow collection, Result, sealed-class matching).
// If a symbol name differs after building in Xcode, fix it HERE in one place.

// MARK: Flow collection

extension SkieSwiftFlow {
    /// Returns the first emitted element (mirrors Kotlin `Flow.firstOrNull()`).
    func firstValue() async -> Element? {
        for await value in self {
            return value
        }
        return nil
    }
}

extension SkieSwiftOptionalFlow {
    /// Returns the first emitted element (for `Flow<T?>`).
    func firstValue() async -> Element? {
        for await value in self {
            return value
        }
        return nil
    }
}

// MARK: Nullable Long boxing
//
// Kotlin `Long?` is exported as a boxed `KotlinLong?`. Non-null `Long` stays `Int64`.

extension Optional where Wrapped == Int64 {
    /// Box an optional Swift Int64 into the `KotlinLong?` a nullable Kotlin `Long?` expects.
    var kotlinLong: KotlinLong? { map { KotlinLong(value: $0) } }
}

extension Int64 {
    /// Box a non-optional Swift Int64 into a `KotlinLong` (assignable to a `KotlinLong?` param).
    var kotlinLong: KotlinLong { KotlinLong(value: self) }
}

// MARK: kotlinx-datetime <-> Foundation.Date

enum KotlinDateTime {

    /// Current wall-clock time as a Kotlin `LocalDateTime`.
    static func now() -> LocalDateTime {
        fromDate(Date())
    }

    /// Swift `Date` -> Kotlin `LocalDateTime` (via epoch millis bridge in DateTimeExt.kt).
    static func fromDate(_ date: Date) -> LocalDateTime {
        let millis = Int64(date.timeIntervalSince1970 * 1000.0)
        return DateTimeExtKt.toLocalDateTime(millis)
    }

    /// Kotlin `LocalDateTime` -> Swift `Date`.
    static func toDate(_ dateTime: LocalDateTime) -> Date {
        let millis = dateTime.toEpochMilliseconds()
        return Date(timeIntervalSince1970: Double(millis) / 1000.0)
    }

    /// Kotlin `LocalDate` -> Swift `Date` (start of day).
    static func toDate(_ date: LocalDate) -> Date {
        toDate(LocalDateTime(date: date, time: LocalTime(hour: 0, minute: 0, second: 0, nanosecond: 0)))
    }

    /// Build a `LocalDateTime` from a `LocalDate` + `LocalTime`.
    static func combine(date: LocalDate, time: LocalTime) -> LocalDateTime {
        LocalDateTime(date: date, time: time)
    }
}

// MARK: Formatting (mirrors extensions/DateTimeExt.kt)

extension LocalTime {
    /// "HH:mm" — mirrors `LocalTime.toUITime()`.
    var uiString: String {
        String(format: "%02d:%02d", Int(hour), Int(minute))
    }
}

extension LocalDateTime {
    /// "HH:mm" — mirrors `LocalDateTime.toTime()`.
    var timeString: String {
        String(format: "%02d:%02d", Int(hour), Int(minute))
    }
}

extension LocalDate {
    /// "dd.MM.yyyy" — mirrors `LocalDate.toUIDate()`.
    var uiString: String {
        String(format: "%02d.%02d.%d", Int(day), Int(monthNumber), Int(year))
    }
}

extension LocalDate {
    /// Sortable/comparable key (kotlinx `LocalDate` isn't Swift `Comparable`).
    var ordinalKey: Int { Int(year) * 10000 + Int(monthNumber) * 100 + Int(day) }
}

extension LocalTime {
    /// Today's date carrying this time — for binding to a SwiftUI `DatePicker`.
    var asDate: Date {
        Calendar.current.date(bySettingHour: Int(hour), minute: Int(minute), second: 0, of: Date()) ?? Date()
    }
    static func from(_ date: Date) -> LocalTime {
        let c = Calendar.current.dateComponents([.hour, .minute], from: date)
        return LocalTime(hour: Int32(c.hour ?? 0), minute: Int32(c.minute ?? 0), second: 0, nanosecond: 0)
    }
}

// MARK: DayOfWeek

extension Kotlinx_datetimeDayOfWeek {
    /// Localized Russian day-of-week name (`ordinal` 0 = Monday ... 6 = Sunday).
    var uiName: String {
        switch ordinal {
        case 0: return "Понедельник"
        case 1: return "Вторник"
        case 2: return "Среда"
        case 3: return "Четверг"
        case 4: return "Пятница"
        case 5: return "Суббота"
        default: return "Воскресенье"
        }
    }

    static var allDays: [Kotlinx_datetimeDayOfWeek] {
        (1...7).map { DateTimeExtKt.getDayOfWeek(isoIndex: Int32($0)) }
    }
}
