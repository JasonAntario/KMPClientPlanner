import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.services.ServicesListScreen` (Home).
struct ServicesListScreen: View {
    let onOpenService: (Int64) -> Void
    let onAddService: () -> Void

    @StateObject private var viewModel = ServicesListViewModel()

    var body: some View {
        VStack(spacing: 8) {
            FilterChipsView(filters: viewModel.filters, current: viewModel.currentFilter,
                            onSelect: viewModel.selectFilter)
                .padding(.top, 8)

            if viewModel.isLoading {
                LoadingScreen()
            } else if viewModel.entries.isEmpty {
                emptyState
            } else {
                list
            }
        }
        .navigationTitle(AppStrings.mainTitle)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onAddService) { Image(systemName: "plus") }
            }
        }
        .onAppear { viewModel.start() }
    }

    private var list: some View {
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

    private var emptyState: some View {
        VStack(spacing: 12) {
            Text(AppStrings.servicesListNoServices).font(.headline)
            Text(AppStrings.servicesListNoServicesDescription)
                .multilineTextAlignment(.center).foregroundColor(.secondary)
        }
        .padding().frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
