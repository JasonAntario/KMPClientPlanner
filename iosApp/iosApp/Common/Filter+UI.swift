import SharedLogic

/// UI helpers for the shared `ServicesFilter` enum (mirrors the `tabs_*` resources).
extension ServicesFilter {
    var uiName: String {
        switch self {
        case .today: return AppStrings.tabsToday
        case .tomorrow: return AppStrings.tabsTomorrow
        case .currentWeek: return AppStrings.tabsCurrentWeek
        case .nextWeek: return AppStrings.tabsNextWeek
        case .currentMonth: return AppStrings.tabsCurrentMonth
        case .nextMonth: return AppStrings.tabsNextMonth
        case .customInterval: return AppStrings.tabsCustomInterval
        }
    }

    static var homeFilters: [ServicesFilter] { ServicesFilterKt.getHomeScreenFilters() }
    static var statisticsFilters: [ServicesFilter] { ServicesFilterKt.getStatisticsScreenFilters() }

    /// (start, end) interval for this filter, or nil for `.customInterval`.
    var dateInterval: (LocalDate, LocalDate)? {
        guard let pair = self.getDateInterval(),
              let first = pair.first as? LocalDate, let second = pair.second as? LocalDate
        else { return nil }
        return (first, second)
    }
}
