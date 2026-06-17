import SharedLogic

/// iOS counterpart of `ui.screens.settings.SettingsScreenState`.
struct SettingsScreenState {
    var serviceType: ServiceType = .base
    var serviceTypeList: [ServiceType] = ServiceType.allCases
    var version: String = "0.1.0"
}

/// One-off effects emitted by the view model — mirrors `SettingsScreenEvent`.
enum SettingsScreenEvent: Equatable {
    case allDataCleared
}
