import Foundation
import SharedLogic

/// iOS counterpart of `ServicesScreenViewModel` (Home services list).
@MainActor
final class ServicesListViewModel: ObservableObject {

    @Published private(set) var isLoading = true
    @Published private(set) var entries: [ServiceListEntry] = []
    @Published private(set) var currentFilter: ServicesFilter = .today

    let filters = ServicesFilter.homeFilters

    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let addEditDeleteServiceUseCase = IosDi.shared.addEditDeleteServiceUseCase
    private var loadTask: Task<Void, Never>?

    func start() {
        guard loadTask == nil else { return }
        loadData()
    }

    private func loadData() {
        loadTask?.cancel()
        loadTask = Task {
            // The use case applies the date-interval filter in Kotlin (getAllServices(filter:)).
            for await services in getServicesUseCase.getAllServices(filter: currentFilter) {
                let clients = await getClientsUseCase.getAllClients().firstValue() ?? []
                let byId = Dictionary(uniqueKeysWithValues: clients.map { ($0.id, $0) })
                entries = buildServiceEntries(services: services) { byId[$0] }
                isLoading = false
            }
        }
    }

    func selectFilter(_ filter: ServicesFilter) {
        currentFilter = filter
        isLoading = true
        loadData()
    }

    func togglePaid(_ item: ServiceRowItem) {
        Task {
            let updated = BaseService(
                id: item.service.id, title: item.service.title, clientId: item.service.clientId,
                startDate: item.service.startDate, endDate: item.service.endDate, address: item.service.address,
                isFinished: item.service.isFinished, isPaid: !item.service.isPaid, price: item.service.price,
                currency: item.service.currency, comment: item.service.comment,
                serviceType: item.service.serviceType, serviceSubtype: item.service.serviceSubtype)
            try? await addEditDeleteServiceUseCase.update(service: updated)
        }
    }

    func toggleFinished(_ item: ServiceRowItem) {
        Task {
            let updated = BaseService(
                id: item.service.id, title: item.service.title, clientId: item.service.clientId,
                startDate: item.service.startDate, endDate: item.service.endDate, address: item.service.address,
                isFinished: !item.service.isFinished, isPaid: item.service.isPaid, price: item.service.price,
                currency: item.service.currency, comment: item.service.comment,
                serviceType: item.service.serviceType, serviceSubtype: item.service.serviceSubtype)
            try? await addEditDeleteServiceUseCase.update(service: updated)
        }
    }

    func delete(_ item: ServiceRowItem) {
        Task { try? await addEditDeleteServiceUseCase.deleteService(serviceId: item.service.id) }
    }

    deinit { loadTask?.cancel() }
}
