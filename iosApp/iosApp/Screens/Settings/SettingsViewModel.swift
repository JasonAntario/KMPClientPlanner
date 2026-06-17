import Foundation
import SharedLogic

/// iOS counterpart of `ui.screens.settings.SettingsViewModel`.
///
/// MVVM: the view observes `state`; intents come in through `handle(_:)`.
/// Dependencies (`AppSettings`, `ClearDatabaseUseCase`) are resolved from the
/// shared Koin graph via `IosDi`. Suspend functions are consumed as Swift
/// `async` thanks to Skie.
@MainActor
final class SettingsViewModel: ObservableObject {

    enum Action {
        case loadData
        case onServiceTypeSelected(ServiceType)
        case deleteAllData
    }

    @Published private(set) var state = SettingsScreenState()

    /// Emitted once when all data has been cleared (consumed by the view).
    @Published var event: SettingsScreenEvent?

    private let appSettings: AppSettings = IosDi.shared.appSettings
    private let clearDatabaseUseCase: ClearDatabaseUseCase = IosDi.shared.clearDatabaseUseCase

    func handle(_ action: Action) {
        switch action {
        case .loadData:
            loadData()
        case .onServiceTypeSelected(let serviceType):
            changeServiceType(serviceType)
        case .deleteAllData:
            deleteAllData()
        }
    }

    private func loadData() {
        Task {
            if let currentServiceType = try? await appSettings.getServiceType() {
                state.serviceType = currentServiceType
            }
        }
    }

    private func changeServiceType(_ serviceType: ServiceType) {
        Task {
            try? await appSettings.saveServiceType(serviceType: serviceType)
            state.serviceType = serviceType
        }
    }

    private func deleteAllData() {
        Task {
            try? await appSettings.clear()
            try? await clearDatabaseUseCase.clearDatabase()
            event = .allDataCleared
        }
    }
}
