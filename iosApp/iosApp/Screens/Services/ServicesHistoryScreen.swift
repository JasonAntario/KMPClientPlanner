import SwiftUI
import SharedLogic

/// iOS counterpart of `ServicesHistoryScreenViewModel`.
@MainActor
final class ServicesHistoryViewModel: ObservableObject {
    @Published private(set) var isLoading = true
    @Published private(set) var entries: [ServiceListEntry] = []

    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let addEditDeleteServiceUseCase = IosDi.shared.addEditDeleteServiceUseCase
    private var loadTask: Task<Void, Never>?

    func load(clientId: Int64) {
        guard loadTask == nil else { return }
        loadTask = Task {
            for await services in getServicesUseCase.getServicesForClientFlow(clientId: clientId) {
                guard let client = await getClientsUseCase.getClientById(clientId: clientId.kotlinLong).firstValue() ?? nil
                else { entries = []; isLoading = false; continue }
                entries = buildServiceEntries(services: services) { _ in client }
                isLoading = false
            }
        }
    }

    private func updated(_ service: BaseService, paid: Bool? = nil, finished: Bool? = nil) -> BaseService {
        BaseService(
            id: service.id, title: service.title, clientId: service.clientId,
            startDate: service.startDate, endDate: service.endDate, address: service.address,
            isFinished: finished ?? service.isFinished, isPaid: paid ?? service.isPaid, price: service.price,
            currency: service.currency, comment: service.comment,
            serviceType: service.serviceType, serviceSubtype: service.serviceSubtype)
    }

    func togglePaid(_ item: ServiceRowItem) {
        Task { try? await addEditDeleteServiceUseCase.update(service: updated(item.service, paid: !item.service.isPaid)) }
    }
    func toggleFinished(_ item: ServiceRowItem) {
        Task { try? await addEditDeleteServiceUseCase.update(service: updated(item.service, finished: !item.service.isFinished)) }
    }
    func delete(_ item: ServiceRowItem) {
        Task { try? await addEditDeleteServiceUseCase.deleteService(serviceId: item.service.id) }
    }

    deinit { loadTask?.cancel() }
}

struct ServicesHistoryScreen: View {
    let clientId: Int64
    let onClose: () -> Void
    let onOpenService: (Int64) -> Void

    @StateObject private var viewModel = ServicesHistoryViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading {
                LoadingScreen()
            } else {
                List {
                    ForEach(viewModel.entries) { entry in
                        switch entry {
                        case .divider(let date):
                            Text("\(date.dayOfWeek.uiName), \(date.uiString)")
                                .font(.headline).foregroundColor(.accentColor)
                                .listRowSeparator(.hidden)
                        case .item(let item):
                            ServiceItemView(
                                item: item,
                                onTap: { onOpenService(item.id) },
                                onTogglePaid: { viewModel.togglePaid(item) },
                                onToggleFinished: { viewModel.toggleFinished(item) },
                                onDelete: { viewModel.delete(item) }
                            )
                        }
                    }
                }
                .listStyle(.plain)
            }
        }
        .navigationTitle(AppStrings.servicesListHistory)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { viewModel.load(clientId: clientId) }
    }
}
