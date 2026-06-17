import SharedLogic

/// UI presentation for the shared `ServiceType` enum.
/// Mirrors `ServiceType.toUIName()` from `sharedUI` (ComposeExt.kt).
extension ServiceType {
    var uiName: String {
        switch self {
        case .base: return AppStrings.serviceTypeBase
        case .education: return AppStrings.serviceTypeEducation
        case .beauty: return AppStrings.serviceTypeBeauty
        case .tattoo: return AppStrings.serviceTypeTattoo
        case .sport: return AppStrings.serviceTypeSport
        }
    }

    /// Autofill service title prefix (mirrors the `service_prefix_*` resources).
    var titlePrefix: String {
        switch self {
        case .education: return "Урок с"
        case .sport: return "Тренировка с"
        case .tattoo: return "Сеанс тату с"
        case .beauty: return "Сеанс с"
        case .base: return "Сессия с"
        }
    }
}
