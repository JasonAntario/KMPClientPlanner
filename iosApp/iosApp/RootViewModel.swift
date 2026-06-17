import Foundation
import SharedLogic

/// Drives the top-level app phase, mirroring `MainScreenViewModel`
/// (`getStartDestination` / `updateServiceType`):
/// no service type -> welcome; no clients -> noClients; otherwise -> main.
@MainActor
final class RootViewModel: ObservableObject {

    enum Phase {
        case loading
        case welcome
        case serviceTypeSelection
        case noClients
        case main
    }

    @Published private(set) var phase: Phase = .loading

    private let appSettings: AppSettings = IosDi.shared.appSettings
    private let getClientsUseCase: GetClientsUseCase = IosDi.shared.getClientsUseCase

    func resolveStartDestination() {
        Task {
            let serviceType = try? await appSettings.getServiceType()
            if serviceType == nil {
                phase = .welcome
                return
            }
            let clients = await getClientsUseCase.getAllClients().firstValue() ?? []
            phase = clients.isEmpty ? .noClients : .main
        }
    }

    func onStartClicked() {
        phase = .serviceTypeSelection
    }

    func onChangeServiceTypeClicked() {
        phase = .serviceTypeSelection
    }

    func onServiceTypeSelected(_ serviceType: ServiceType) {
        Task {
            try? await appSettings.saveServiceType(serviceType: serviceType)
            resolveStartDestination()
        }
    }

    func onAllDataCleared() {
        phase = .welcome
    }

    /// Called after a client is added from the empty state, so we re-evaluate.
    func reevaluate() {
        resolveStartDestination()
    }
}
