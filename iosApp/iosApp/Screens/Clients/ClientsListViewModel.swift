import Foundation
import SharedLogic

/// iOS counterpart of `ClientsScreenViewModel`.
@MainActor
final class ClientsListViewModel: ObservableObject {

    enum Item: Identifiable {
        case divider(String)
        case client(BaseClient)

        var id: String {
            switch self {
            case .divider(let letter): return "divider-\(letter)"
            case .client(let client): return "client-\(client.id)"
            }
        }
    }

    @Published private(set) var isLoading = true
    @Published private(set) var items: [Item] = []

    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let addEditDeleteClientUseCase = IosDi.shared.addEditDeleteClientUseCase
    private var loadTask: Task<Void, Never>?

    func start() {
        guard loadTask == nil else { return }
        loadTask = Task {
            for await clients in getClientsUseCase.getAllClients() {
                // Group by first letter, mirroring the Compose view model.
                let grouped = Dictionary(grouping: clients) { String($0.name.prefix(1)) }
                var result: [Item] = []
                for letter in grouped.keys.sorted() {
                    result.append(.divider(letter))
                    result.append(contentsOf: (grouped[letter] ?? []).map { Item.client($0) })
                }
                items = result
                isLoading = false
            }
        }
    }

    func deleteClient(_ client: BaseClient) {
        Task { try? await addEditDeleteClientUseCase.deleteClient(clientId: client.id) }
    }

    deinit { loadTask?.cancel() }
}
