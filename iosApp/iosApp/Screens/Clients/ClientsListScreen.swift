import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.clients.ClientsListScreen`.
struct ClientsListScreen: View {
    let onOpenClient: (Int64) -> Void
    let onAddClient: () -> Void

    @StateObject private var viewModel = ClientsListViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading {
                LoadingScreen()
            } else if viewModel.items.isEmpty {
                emptyState
            } else {
                list
            }
        }
        .navigationTitle(AppStrings.clientsTitle)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onAddClient) { Image(systemName: "plus") }
            }
        }
        .onAppear { viewModel.start() }
    }

    private var list: some View {
        List {
            ForEach(viewModel.items) { item in
                switch item {
                case .divider(let letter):
                    Text(letter)
                        .font(.headline)
                        .foregroundColor(.accentColor)
                        .listRowSeparator(.hidden)
                case .client(let client):
                    Button { onOpenClient(client.id) } label: {
                        ClientRow(client: client)
                    }
                    .buttonStyle(.plain)
                    .swipeActions {
                        Button(role: .destructive) {
                            viewModel.deleteClient(client)
                        } label: { Label("", systemImage: "trash") }
                    }
                }
            }
        }
        .listStyle(.plain)
    }

    private var emptyState: some View {
        VStack(spacing: 12) {
            Text(AppStrings.clientsListNoClients).font(.headline)
            Text(AppStrings.clientsListNoClientsDescription)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct ClientRow: View {
    let client: BaseClient

    var body: some View {
        HStack(spacing: 12) {
            ShortNameBoxView(text: client.getShortName(), size: 44)
            VStack(alignment: .leading, spacing: 2) {
                Text(client.getFullName()).font(.body)
                if let price = client.getFormattedPrice() {
                    Text(price).font(.caption).foregroundColor(.secondary)
                }
            }
            Spacer()
        }
        .contentShape(Rectangle())
    }
}
